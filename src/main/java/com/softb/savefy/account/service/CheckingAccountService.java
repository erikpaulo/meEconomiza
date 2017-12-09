package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.security.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class CheckingAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Inject
    private UserAccountService userAccountService;

    /**
     * Update or create the account date, verifying if it belongs to the current user
     * @param account
     * @param groupId
     * @return
     */
    public CheckingAccount save(CheckingAccount account, Integer groupId){
        account = accountRepository.save(account);
        calcAccountBalance(account);

        return account;
    }

    /**
     * Save an entry.
     * @param entry
     * @return
     */
    public CheckingAccountEntry saveEntry(CheckingAccountEntry entry, Integer groupId){
        if (!entry.getGroupId().equals(groupId)){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return accountEntryRepository.save(entry);
    }

    /**
     * Return this entry if the current user has it
     * @param entryId
     * @param groupId
     * @return
     */
    public CheckingAccountEntry getEntry(Integer entryId, Integer groupId){
        return accountEntryRepository.findOne(entryId, groupId);
    }

    /**
     * Return this entry if the current user has it
     * @param entryId
     * @return
     */
    public void delEntry(Integer entryId){
        try{
            accountEntryRepository.delete(entryId);
        } catch(DataIntegrityViolationException e){
            throw new BusinessException("Lançamentos importados não podem ser removidos.");
        }
    }

    /**
     * Return one account with no detail
     * @param accountId
     * @param groupId
     * @return
     */
    public Account get(Integer accountId, Integer groupId){
    		Account account = accountRepository.findOne(accountId, groupId);
    		return account;
    }

    /**
     * Return this account with all its related and pre processed data
     * @param accountId Account
     * @param groupId Current User
     * @return
     */
    public CheckingAccount getAccount(Integer accountId, Integer groupId){
        CheckingAccount account = (CheckingAccount) accountRepository.findOne(accountId, groupId);
        calcAccountBalance(account);


        // For those conciliations not yet imported, update the entry info that indicates if it should be imported or not.
        for (Conciliation conciliation: account.getConciliations()) {
            if (!conciliation.getImported()){
                for (ConciliationEntry entry: conciliation.getEntries()) {

                    // Check if there is an account entry that has the sabe date and amount as this conciliation entry.
                    List<CheckingAccountEntry> conflicts =  accountEntryRepository.listAllByDateAmount(groupId, accountId, entry.getDate(), entry.getAmount());
                    if (conflicts.size() > 0) {
                        entry.setExists(true);
                    }
                }
            }
        }

        // Sort Conciliations DESC
        Collections.sort(account.getConciliations(), new Comparator<Conciliation>(){
            public int compare(Conciliation o1, Conciliation o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        // Sort entries ASC
        Collections.sort(account.getEntries(), new Comparator<CheckingAccountEntry>(){
            public int compare(CheckingAccountEntry o1, CheckingAccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        calcAccountBalance(account);
        return account;
    }

    protected void calcAccountBalance(CheckingAccount checkingAccount) {
        // Set the account balance based in its entries;
        Double balance = 0.0;

        balance += checkingAccount.getStartBalance();
        if (checkingAccount.getEntries() != null){
            for (CheckingAccountEntry entry: checkingAccount.getEntries()){
                balance += entry.getAmount();
                entry.setBalance(balance);
            }
        }
        checkingAccount.setBalance(balance);
    }

    public void deleteEntries(List<CheckingAccountEntry> entries, Integer groupId){
        accountEntryRepository.delete(entries);
    }

    public List<Institution> getInstitutions(){
        return institutionRepository.findAll();
    }
}
