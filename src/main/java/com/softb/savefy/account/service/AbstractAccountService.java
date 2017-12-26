package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.AccountEntry;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Calendar;

public abstract class AbstractAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    protected abstract void calcAccountBalance(Account checkingAccount);

    /**
     * Just get the account, with no calculation
     * @param accountId
     * @param groupId
     * @return
     */
    public Account get(Integer accountId, Integer groupId) {
        return accountRepository.findOne(accountId, groupId);
    }

    /**
     * Save this entry and update Account's last update date
     * @param entry
     * @param groupId
     * @return
     */
    public AccountEntry save(AccountEntry entry, Integer groupId) {
        AccountEntry accountEntry = accountEntryRepository.save( entry );
        updateLastUpdate( accountEntry.getAccountId(), groupId );

        return accountEntry;
    }

    /**
     * Update the last update of this account
     * @param accountId
     * @param groupId
     */
    public void updateLastUpdate(Integer accountId, Integer groupId) {
        Account account = accountRepository.findOne(accountId, groupId);
        account.setLastUpdate(Calendar.getInstance().getTime());
        accountRepository.save(account);
    }

    /**
     * Return this entry if the current user has it
     * @param entryId
     * @param groupId
     * @return
     */
    public AccountEntry getEntry(Integer entryId, Integer groupId){
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
}
