package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.CheckingAccount;
import com.softb.savefy.account.model.Institution;
import com.softb.savefy.account.model.InvestimentAccount;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class AccountsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Inject
    private CheckingAccountService checkingAccountService;

    @Inject
    private InvestmentAccountService investmentAccountService;

    /**
     * Return all current active user accounts. Calculate the account balance for presentation purposes
     * @param groupId
     * @return
     */
    public List<Account> getAllActiveAccounts(Integer groupId){
    		List<Account> accounts = accountRepository.findAllActive(groupId);

    		// Calculate account balance
        for (Account account: accounts) {
            if (account.getType().equals(Account.Type.CKA)){
                checkingAccountService.calcAccountBalance((CheckingAccount) account);
                ((CheckingAccount)account).setEntries(null);
            } else if (account.getType().equals(Account.Type.INV)){
                investmentAccountService.calcAccountBalance((InvestimentAccount) account);
            }
        }

    		return accounts;
    }

    /**
     * Return one account with no detail
     * @param accountId
     * @param groupId
     * @return
     */
    public void inactivate(Integer accountId, Integer groupId){
        Account account = accountRepository.findOne(accountId, groupId);
        if (account == null) {
            throw new BusinessException("Conta não encontrada para usuário corrente.");
        }

        account.setActivated(false);
        accountRepository.save(account);

    }

    public List<Institution> getInstitutions(){
        return institutionRepository.findAll();
    }

    /**
     * Return all trasferable accounts.
     * @param groupId
     * @return
     */
    public List<Account> getAllTransferable(Integer groupId) {
        List<Account> returnList = new ArrayList<>();

        List<Account> accounts = accountRepository.findAllActive(groupId);
        for (Account account: accounts) {
            if (account.getType().equals(Account.Type.CKA)
                || account.getType().equals(Account.Type.CCA)){
                returnList.add(account);
            }
        }

        return returnList;
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
}
