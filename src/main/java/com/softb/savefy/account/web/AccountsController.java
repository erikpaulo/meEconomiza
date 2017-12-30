package com.softb.savefy.account.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.Institution;
import com.softb.savefy.account.service.AccountsService;
import com.softb.savefy.account.service.ConciliationService;
import com.softb.savefy.preferences.services.UserPreferencesService;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController("AppAccountsController")
@RequestMapping("/api/account")
public class AccountsController extends AbstractRestController<Account, Integer> {

    @Inject
    private AccountsService accountService;

    @Inject
    private ConciliationService conciliationService;

    @Inject
    private UserPreferencesService userPreferencesService;


    /**
     * Lists all account registered for this user, but its stocks aren't loaded.
     *
     * @return List Accounts without its stocks
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Account> listAll() {
        List<Account> accounts = accountService.getAllActiveAccounts(getGroupId());
        return accounts;
    }

    /**
     * Inactivate this account
     * @param id
     */
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        accountService.inactivate(id, getGroupId());
    }

    @RequestMapping(value="/institutions", method = RequestMethod.GET)
    @ResponseBody public List<Institution> getInstitutions( ){
        return accountService.getInstitutions();
    }

    @RequestMapping(value="/transferable", method = RequestMethod.GET)
    @ResponseBody public List<Account> getAllForTransferable( ){
        return accountService.getAllTransferable(getGroupId());
    }



}

