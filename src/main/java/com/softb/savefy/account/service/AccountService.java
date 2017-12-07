package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.CheckingAccountEntry;
import com.softb.savefy.account.model.CheckingAccount;
import com.softb.savefy.account.model.Institution;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.InstitutionRepository;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Inject
    private CheckingAccountService checkingAccountService;

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
    public Account get(Integer accountId, Integer groupId){
    		Account account = accountRepository.findOne(accountId, groupId);
    		return account;
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

    public void deleteEntries(List<CheckingAccountEntry> entries, Integer groupId){
        accountEntryRepository.delete(entries);
    }

    public List<Institution> getInstitutions(){
        return institutionRepository.findAll();
    }
}
