package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class BenefitsAccountService extends CheckingAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    /**
     * Retorna contas corrente ativas.
     * @param groupId
     * @return
     */
    public List<Account> getAllActiveAccounts(Integer groupId) {
        return super.getAllActiveAccounts(groupId);
    }

    /**
     * Update or create the account
     * @param account
     * @param groupId
     * @return
     */
    public CheckingAccount saveAccount(CheckingAccount account, Integer groupId){
        return super.saveAccount(account, groupId);
    }

    /**
     * Save an entry, checking if it belongs to current user.
     * @param entry
     * @return
     */
    public CheckingAccountEntry updateEntry(BenefitsAccountEntry entry, Integer groupId){
        return super.updateEntry(entry, groupId);
    }

    /**
     * Return this account with all its related and pre processed data
     * @param accountId Account
     * @param groupId Current User
     * @return
     */
    public BenefitsAccount getAccount(Integer accountId, Integer groupId){
        return (BenefitsAccount) super.getAccount(accountId, groupId);
    }

    /**
     * Calculate the account balance
     * @param account
     */
    @Override
    protected void calcAccountBalance(Account account) {
        super.calcAccountBalance(account);
    }


    /**
     * Used by conciliation process
     * @param entriesToDelete
     * @param groupId
     */
    public void deleteEntries(List<CheckingAccountEntry> entriesToDelete, Integer groupId) {
        super.deleteEntries(entriesToDelete, groupId);
    }

}
