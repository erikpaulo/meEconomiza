package com.softb.meeconomiza.account.service;

import com.softb.meeconomiza.account.model.AccountEntry;
import com.softb.meeconomiza.account.model.Conciliation;
import com.softb.meeconomiza.account.model.ConciliationEntry;
import com.softb.meeconomiza.account.repository.AccountEntryRepository;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class ConciliationService {

//    public static final String GROUP_ENTRIES_BY_MONTH = "MONTH";

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    public Conciliation uploadEntries(Integer accountId, final HttpServletRequest request, final HttpServletResponse response, Integer groupId)
            throws SystemException, DataAccessException, FileUploadException, IOException, ParseException {

        Conciliation conciliation = new Conciliation();

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
                                                        record.get(1), null, nf.parse(record.get(2)).doubleValue(), false,
                                                        null, groupId);

                // Verifica se existe um lançamento na conta com mesma data e valor. Se existir aponta como provável conflito.
                List<AccountEntry> conflicts =  accountEntryRepository.listAllByDateAmount(groupId, accountId, entryToImport.getDate(), entryToImport.getAmount());
                if (conflicts.size() > 0) {
                    entryToImport.setExists(true);
                }

                entriesToImport.add(entryToImport);
            }
            reader.close();
        }

        return entriesToImport;
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

}
