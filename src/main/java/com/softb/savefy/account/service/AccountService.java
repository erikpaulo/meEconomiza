package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.AccountEntry;
import com.softb.savefy.account.model.Conciliation;
import com.softb.savefy.account.model.ConciliationEntry;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.security.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class AccountService {

    public static final String GROUP_ENTRIES_BY_MONTH = "MONTH";

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Inject
    private UserAccountService userAccountService;

    /**
     * Update or create the account date, verifying if it belongs to the current user
     * @param account
     * @param groupId
     * @return
     */
    public Account save(Account account, Integer groupId){
        if (!account.getGroupId().equals(groupId)){
            throw new BusinessException("This account does not belong to the current user.");
        }

        return accountRepository.save(account);
    }

    /**
     * Return all current active user accounts. Calculate the account balance for presentation purposes
     * @param groupId
     * @return
     */
    public List<Account> getAllActiveAccounts(Integer groupId){
    		List<Account> accounts = accountRepository.findAllActive(groupId);

    		// Calculate account balance
        for (Account account: accounts) {
            calcAccountBalance(account);

            // Set entries null to minimiza data transfer
            account.setEntries(null);
        }

    		return accounts;
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
    public Account getAccount(Integer accountId, Integer groupId){
        Account account = accountRepository.findOne(accountId, groupId);
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

        return account;
    }

    private void calcAccountBalance(Account account) {
        // Set the account balance based in its entries;
        Double balance = 0.0;
        if (account.getEntries() != null){
            for (AccountEntry entry: account.getEntries()){
                balance += entry.getAmount();
            }
        }
        account.setBalance(account.getStartBalance() + balance);
    }

    public void deleteEntries(List<AccountEntry> entries, Integer groupId){
        accountEntryRepository.delete(entries);
    }
}
