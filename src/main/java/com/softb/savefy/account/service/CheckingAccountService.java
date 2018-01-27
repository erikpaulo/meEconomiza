package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.savefy.utils.AppMaths;
import com.softb.system.errorhandler.exception.SystemException;
import com.softb.system.security.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class CheckingAccountService extends AbstractAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Inject
    private UserAccountService userAccountService;

    /**
     * Retorna contas corrente ativas.
     * @param groupId
     * @return
     */
    public List<Account> getAllActiveAccounts(Integer groupId) {
        List<Account> retList = new ArrayList<>();

        List<Account> accounts = accountRepository.findAllActive(groupId);
        for (Account account: accounts) {
            if (account.getType().equals(Account.Type.CKA)){
                retList.add(account);
            }
        }

        return retList;
    }

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
        CheckingAccountEntry currentEntry = (CheckingAccountEntry) accountEntryRepository.findOne( entry.getId(), groupId );

        // Check if it's a transfer
        if (currentEntry!=null && currentEntry.getTransfer()){
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
        return (CheckingAccountEntry) save(entry, groupId);
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
                    List<AccountEntry> conflicts =  accountEntryRepository.listAllByDateAmount(groupId, accountId, entry.getDate(), entry.getAmount());
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

        // Sort stocks ASC
        Collections.sort(account.getEntries(), new Comparator<AccountEntry>(){
            public int compare(AccountEntry o1, AccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        calcAccountBalance(account);
        return account;
    }

    /**
     * Calculate the account balance
     * @param account
     */
    @Override
    protected void calcAccountBalance(Account account) {
        CheckingAccount checkingAccount = (CheckingAccount) account;

        Double balance = checkingAccount.getStartBalance();
        if (checkingAccount.getEntries() != null){
            for (CheckingAccountEntry entry: checkingAccount.getEntries()){
                balance += entry.getAmount();
                entry.setBalance(balance);
            }
        }
        checkingAccount.setBalance(AppMaths.round(balance,2));
    }


    /**
     * Used by conciliation process
     * @param entriesToDelete
     * @param groupId
     */
    public void deleteEntries(List<CheckingAccountEntry> entriesToDelete, Integer groupId) {
    }
    //        return accountRepository.findOne(accountId, groupId);
    //    public Account get(Integer accountId, Integer groupId) {
    //     */
    //     * @return
    //     * @param groupId
    //     * @param accountId
    //     * Used by conciliation process
//    /**

//    }

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
        return (CheckingAccountEntry) save((AccountEntry)twinEntry, groupId);
    }

    private void removeTwinEntry(CheckingAccountEntry entry, CheckingAccountEntry currentEntry) {
        accountEntryRepository.delete( currentEntry.getTwinEntryId() );
        entry.setTwinEntryId( null );
        entry.setAccountDestinyId( null );
    }

    private void updateTwinEntry(CheckingAccountEntry entry) {
        CheckingAccountEntry twinEntry = (CheckingAccountEntry) accountEntryRepository.findOne( entry.getTwinEntryId() );
        twinEntry.setAmount( entry.getAmount() * -1 );
        twinEntry.setDate( entry.getDate() );
        twinEntry.setSubCategory( entry.getSubCategory() );
        accountEntryRepository.save( twinEntry );
    }
}
