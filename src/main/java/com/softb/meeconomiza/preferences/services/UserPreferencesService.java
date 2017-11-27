package com.softb.meeconomiza.account.service;

import com.softb.meeconomiza.account.model.Account;
import com.softb.meeconomiza.account.model.AccountEntry;
import com.softb.meeconomiza.account.model.Conciliation;
import com.softb.meeconomiza.account.model.ConciliationEntry;
import com.softb.meeconomiza.account.repository.AccountEntryRepository;
import com.softb.meeconomiza.account.repository.ConciliationEntryRepository;
import com.softb.meeconomiza.account.repository.ConciliationRepository;
import com.softb.meeconomiza.categorization.service.CategoryPredictionService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.SystemException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.time.DateUtils;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class ConciliationService {

    @Autowired
    private ConciliationEntryRepository conciliationEntryRepository;

    @Autowired
    private ConciliationRepository conciliationRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Inject
    private CategoryPredictionService categoryPredictionService;

    @Inject
    private AccountService accountService;

    public Conciliation get(Integer conciliationId, Integer groupId, Boolean recheckConflicts){
        Conciliation conciliation = conciliationRepository.findOne(conciliationId, groupId);

        // Check for conflicts
        if (recheckConflicts){
            for (ConciliationEntry entry: conciliation.getEntries()) {
                checkConflicts(conciliation.getAccountId(), entry, groupId);
            }
        }

        // Sort Entries DESC
        Collections.sort(conciliation.getEntries(), new Comparator<ConciliationEntry>(){
            public int compare(ConciliationEntry o1, ConciliationEntry o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });
        return conciliation;
    }

    public Conciliation uploadEntries(Integer accountId, final HttpServletRequest request, final HttpServletResponse response, Integer groupId)
            throws SystemException, DataAccessException, FileUploadException, IOException, ParseException {

        Conciliation conciliation = new Conciliation(Calendar.getInstance().getTime(), accountId, null, false, groupId);

        request.setCharacterEncoding("utf-8");
        if (request.getHeader("Content-Type") != null){
            if  (request.getHeader("Content-Type").startsWith("multipart/form-data")) {

                // open file stream and prepare the file to be manipulated.
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator fileIterator = upload.getItemIterator(request);
                conciliation.setEntries(csvImport(accountId, fileIterator, groupId));
            } else {
                throw new SystemException("Invalid Content-Type: "+ request.getHeader("Content-Type"));
            }
        }

        return conciliation;
    }

    /**
     * Responsável por processar arquivos com lançamentos do tipo CSV separados por ;.
     * @param accountId Número da conta onde os lançamentos serão feitos.
     * @param fileIterator Arquivo.
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws FileUploadException
     * @throws DataAccessException
     */
    public List<ConciliationEntry> csvImport(Integer accountId, FileItemIterator fileIterator, Integer groupId) throws DataAccessException, FileUploadException, IOException, ParseException{
        List<ConciliationEntry> entriesToImport = new ArrayList<>();
        ConciliationEntry entryToImport = null;
        String[] dateFormat = {"dd/MM/yyyy'T'HH:mm:ss"};
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt","BR"));

        // Caso tenha sido enviado mais de um arquivo, itera por eles.
        while (fileIterator.hasNext()) {
            FileItemStream stream = fileIterator.next();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream.openStream()));

            // Recupera o delimitador do arquivo.
            char delimiter = defineDelimiter(reader);

            for(CSVRecord record : CSVFormat.EXCEL.withDelimiter(delimiter).parse(reader).getRecords()) {
                entryToImport = new ConciliationEntry(  DateUtils.parseDate(record.get(0)+"T03:00:00", dateFormat),
                                                        record.get(1), categoryPredictionService.getCategoryForDescription(record.get(1), groupId),
                                                        null, nf.parse(record.get(2)).doubleValue(), false, false, false, null, groupId);

                checkConflicts(accountId, entryToImport, groupId);
                checkInstallmentEntry(entryToImport);
                entriesToImport.add(entryToImport);
            }
            reader.close();
        }

        return entriesToImport;
    }

    private void checkInstallmentEntry(ConciliationEntry entryToImport) {
        String description = entryToImport.getDescription();
        if (description.matches(".*[\\d{2}/\\d{2}]")) {
            entryToImport.setInstallment(true);
        }
    }

    private void checkConflicts(Integer accountId, ConciliationEntry entryToImport, Integer groupId) {
        // Verifica se existe um lançamento na conta com mesma data e valor. Se existir aponta como provável conflito.
        List<AccountEntry> conflicts =  accountEntryRepository.listAllByDateAmount(groupId, accountId, entryToImport.getDate(), entryToImport.getAmount());
        if (conflicts.size() > 0) {
            entryToImport.setExists(true);
            entryToImport.setReject(true);
        }
    }

    /**
     * Save the current state of the conciliation. It's not imported yet.
     * @param conciliation
     * @param groupId
     * @return
     */
    public Conciliation save(Conciliation conciliation, Integer groupId){

        conciliationEntryRepository.save(conciliation.getEntries());
        Conciliation conciliationRet = conciliationRepository.save(conciliation);

        return conciliationRet;
    }

    public Conciliation syncEntriesIntoAccount(Conciliation conciliation, Integer groupId){

        if (conciliation.getImported()){
            throw new BusinessException("This conciliation has already been imported!");
        }

        for (ConciliationEntry entry: conciliation.getEntries()) {
            if (!entry.getReject()){
                // Update categorization for future predictions;
                categoryPredictionService.register(entry.getDescription(), entry.getSubCategory(), groupId);

                // Create account entries
                AccountEntry accEntry = new AccountEntry(entry.getDate(), entry.getSubCategory(), entry.getAmount(), false, conciliation.getAccountId(), null, groupId);
                accEntry = accountEntryRepository.save(accEntry);

                entry.setAccountEntry(accEntry);
            }
        }

        // Update Account
        Account account = accountService.getAccountDetail(conciliation.getAccountId(), groupId);
        account.setLastUpdate(Calendar.getInstance().getTime());
        accountService.save(account, groupId);

        // Remove old conciliations
        Integer count = 0;
        for (Conciliation accConciliation: account.getConciliations()) {
            if (accConciliation.getImported()){
                if (++count > 2){
                    conciliationRepository.delete(accConciliation);
                }

            }
        }

        // Finally, save this conciliation.
        conciliation.setImported(true);
        return save(conciliation, groupId);
    }

    /**
     * Verifica se o delimintador do arquivo CSV é ; ou ,.
     * @param reader Stream do arquivo
     * @return Delimitador do arquivo
     * @throws IOException
     */
    private char defineDelimiter(BufferedReader reader) throws IOException {
        reader.mark(1000);
        char delimiter = (reader.readLine().split(";").length > 1 ? ';' : ',');
        reader.reset();

        return delimiter;
    }

    /**
     * Remove a conciliation informed, but first checks if it belongs to the current user and if its status is 'draft'
     * @param conciliationId
     * @param groupId
     * @return
     */
    public void delete(Integer conciliationId, Integer groupId) {
        Conciliation conciliation = conciliationRepository.findOne(conciliationId, groupId);

        if (conciliation.getImported()) throw new BusinessException("Esta conciliação já foi sincronizada no sistema e não pode ser removida.");
        if (!conciliation.getGroupId().equals(groupId)) throw new BusinessException("Esta conciliação não pertence ao usuário corrente");
        conciliationEntryRepository.delete(conciliation.getEntries());
        conciliationRepository.delete(conciliation);
    }

    public Conciliation rollback(Integer conciliationId, Integer groupId) {
        List<AccountEntry> entriesToDelete = new ArrayList<>();

        Conciliation conciliation = get(conciliationId, groupId, false);
        for (ConciliationEntry entry: conciliation.getEntries()) {
            if (entry.getAccountEntry()!=null){
                entriesToDelete.add(entry.getAccountEntry());
                entry.setAccountEntry(null);
            }

        }
        accountService.deleteEntries(entriesToDelete, groupId);

        //After remove, recheck new conflicts.
        for (ConciliationEntry entry: conciliation.getEntries()) {
            checkConflicts(conciliation.getAccountId(), entry, groupId);
        }

        conciliation.setImported(false);
        conciliationRepository.save(conciliation);

        return conciliation;
    }
}
