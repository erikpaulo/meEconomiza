package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
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
    private CreditCardAccountService creditCardAccountService;

    @Inject
    private InvestmentAccountService investmentAccountService;

    @Inject
    private StockAccountService stockAccountService;

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
                checkingAccountService.calcAccountBalance(account);
            } else if (account.getType().equals(Account.Type.INV)){
                investmentAccountService.calcAccountBalance(account);
            } else if (account.getType().equals(Account.Type.CCA)){
                creditCardAccountService.calcAccountBalance(account);
            } else if (account.getType().equals(Account.Type.STK)){
                stockAccountService.calcAccountBalance(account);
            }
        }

    		return accounts;
    }

    public Boolean isInvestType (Account account){
        return (account.getType().equals(Account.Type.INV)) || (account.getType().equals(Account.Type.STK));
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
}
