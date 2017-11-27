package com.softb.meeconomiza.account.service;

import com.softb.meeconomiza.account.model.Account;
import com.softb.meeconomiza.account.model.AccountEntry;
import com.softb.meeconomiza.account.repository.AccountRepository;
import com.softb.system.security.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class AccountService {

    public static final String GROUP_ENTRIES_BY_MONTH = "MONTH";

    @Autowired
    private AccountRepository accountRepository;

//    @Autowired
//    private AccountEntryRepository accountEntryRepository;

    @Inject
    private UserAccountService userAccountService;




//    /**
//     * Return the user balance, considering all his accounts, including his investments.
//     * @param date
//     * @param groupId
//     * @return
//     */
//    public Double getBalanceUntilDate(Date date, Integer groupId) {
//        List<Account> accounts = accountRepository.findAllByUser(groupId);
//        Double initialBalance = 0.0;
//        for (Account account: accounts) {
//            initialBalance+= account.getStartBalance();
//        }
//
//        Double accBalance = accountEntryRepository.getBalanceByDate( date, groupId );
//        Double balance = (accBalance != null ? accBalance : 0);
//        balance += investmentService.getBalanceUntilDate( date, groupId );
//        return balance;
//    }


//    /**
//     * Return the amount of money saved by the current user between the two informed dates.
//     * @param start
//     * @param end
//     * @param groupId
//     * @return
//     */
//    public Double getSavingsByPeriod(Date start, Date end, Integer groupId) {
//        Double balance = accountEntryRepository.getBalanceByPeriod( start, end, groupId );
//        return balance;
//    }
    
    /**
     * Return all current active user accounts
     * @param groupId
     * @return
     */
    public List<Account> getAllActiveAccounts(Integer groupId){
    		List<Account> accounts = accountRepository.findAllActiveByUser(groupId);

    		// Set the account balance based in its entries;
    		for (Account account: accounts){
    		    Double balance = 0.0;
    		    if (account.getEntries() != null){
    		        for (AccountEntry entry: account.getEntries()){
    		            balance += entry.getAmount();
                    }
                }
                account.setBalance(account.getStartBalance() + balance);
            }

    		return accounts;
    }
}
