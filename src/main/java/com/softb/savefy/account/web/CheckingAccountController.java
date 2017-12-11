package com.softb.savefy.account.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.CheckingAccount;
import com.softb.savefy.account.model.CheckingAccountEntry;
import com.softb.savefy.account.service.CheckingAccountService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.FormValidationError;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;

@RestController("AppCheckingAccountController")
@RequestMapping("/api/account/CKA")
public class CheckingAccountController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "CheckingAccount";

    public static final String OBJECT_ENTRY_NAME = "CheckingAccountEntry";

    @Inject
    private CheckingAccountService checkingAccountService;


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public Account create(@RequestBody CheckingAccount checkingAccount) {

        checkingAccount.setGroupId(getGroupId());
        checkingAccount.setActivated(true);
        checkingAccount.setLastUpdate(Calendar.getInstance().getTime());

        validate(OBJECT_NAME, checkingAccount);
        return checkingAccountService.saveAccount(checkingAccount, getGroupId());
    }

    /**
     * Returns the informed account with no entry.
     * @param checkingAccount Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccount save(@RequestBody CheckingAccount checkingAccount) throws FormValidationError {
        CheckingAccount account = checkingAccountService.saveAccount(checkingAccount, getGroupId());
        return account;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public Account getSummarized(@PathVariable Integer id) throws FormValidationError {
        CheckingAccount account = checkingAccountService.getAccount(id, getGroupId());
        account.setEntries(null);
        return account;
    }

    /**
     * Returns the informed account with no entry.
     * @param id Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
    @ResponseBody public CheckingAccount getDetailed(@PathVariable Integer id) throws FormValidationError {
        CheckingAccount account = checkingAccountService.getAccount(id, getGroupId());
        return account;
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry editEntry(@RequestBody CheckingAccountEntry entry) throws FormValidationError {
        if (!entry.getGroupId().equals(getGroupId())){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return checkingAccountService.updateEntry(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry createEntry(@RequestBody CheckingAccountEntry entry) throws FormValidationError {
        entry.setGroupId(getGroupId());

        validate(OBJECT_ENTRY_NAME, entry);
        return checkingAccountService.updateEntry(entry, getGroupId());
    }


    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeEntry(@PathVariable Integer id) throws FormValidationError {
        CheckingAccountEntry entry = checkingAccountService.getEntry(id, getGroupId());
        if (entry == null){
            throw new BusinessException("Lançamento não encontrado para usuário corrente.");
        }
        checkingAccountService.delEntry(id);
    }

}

