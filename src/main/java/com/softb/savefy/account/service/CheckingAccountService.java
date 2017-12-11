package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.SystemException;
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

    @Inject
    private AccountService accountService;

    /**
     * Update or create the account
     * @param account
     * @param groupId
     * @return
     */
    public CheckingAccount saveAccount(CheckingAccount account, Integer groupId){
        account = accountRepository.save(account);
        calcAccountBalance(account);

        return account;
    }

    /**
     * Save an entry, checking if it belongs to current user.
     * @param entry
     * @return
     */
    public CheckingAccountEntry updateEntry(CheckingAccountEntry entry, Integer groupId){
        CheckingAccountEntry currentEntry = accountEntryRepository.findOne( entry.getId(), groupId );

        // Check if it's a transfer
        if (currentEntry.getTransfer()){
            if (!entry.getTransfer()) { // Needs to delete its twin entry.
                removeTwinEntry( entry, currentEntry );
            } else {
                updateTwinEntry( entry );
            }

        } else {
            if (entry.getTransfer()) { // Needs to create its twin entry.
                CheckingAccountEntry twinEntry = createTwinEntry( entry, groupId );
                entry.setTwinEntryId( twinEntry.getId() );
            }
        }
        return save(entry, groupId);
    }

    private CheckingAccountEntry save(CheckingAccountEntry entry, Integer groupId) {
        CheckingAccountEntry accountEntry = accountEntryRepository.save( entry );
        accountService.updateLastUpdate( accountEntry.getAccountId(), groupId );

        return accountEntry;
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
        Collections.sort(account.getEntries(), new Comparator<AccountEntry>(){
            public int compare(AccountEntry o1, AccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        calcAccountBalance(account);
        return account;
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
     * Calculate the account balance
     * @param checkingAccount
     */
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

    private CheckingAccountEntry createTwinEntry(CheckingAccountEntry entry, Integer groupId) {
        CheckingAccountEntry twinEntry;
        try {
            twinEntry = entry.clone();
        } catch (CloneNotSupportedException e) {
            throw new SystemException(e.getMessage());
        }
        twinEntry.setAmount( twinEntry.getAmount() * -1 );
        twinEntry.setTwinEntryId( entry.getId() );
        twinEntry.setAccountId( entry.getAccountDestinyId() );
        twinEntry.setAccountDestinyId( entry.getAccountId() );
        return save(twinEntry, groupId);
    }

    private void removeTwinEntry(CheckingAccountEntry entry, CheckingAccountEntry currentEntry) {
        accountEntryRepository.delete( currentEntry.getTwinEntryId() );
        entry.setTwinEntryId( null );
        entry.setAccountDestinyId( null );
    }

    private void updateTwinEntry(CheckingAccountEntry entry) {
        CheckingAccountEntry twinEntry = accountEntryRepository.findOne( entry.getTwinEntryId() );
        twinEntry.setAmount( entry.getAmount() * -1 );
        twinEntry.setDate( entry.getDate() );
        twinEntry.setSubCategory( entry.getSubCategory() );
        accountEntryRepository.save( twinEntry );
    }

    /**
     * Used by conciliation process
     * @param entriesToDelete
     * @param groupId
     */
    public void deleteEntries(List<CheckingAccountEntry> entriesToDelete, Integer groupId) {
    }

    /**
     * Used by conciliation process
     * @param accountId
     * @param groupId
     * @return
     */
    public Account get(Integer accountId, Integer groupId) {
        return accountRepository.findOne(accountId, groupId);
    }
}
