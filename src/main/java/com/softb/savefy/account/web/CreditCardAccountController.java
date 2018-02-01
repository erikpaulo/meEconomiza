package com.softb.savefy.account.web;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.service.CreditCardAccountService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.FormValidationError;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

@RestController("AppCreditCardAccountController")
@RequestMapping("/api/account/CCA")
public class CreditCardAccountController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "CreditCardAccount";

    public static final String OBJECT_ENTRY_NAME = "CreditCardAccountEntry";

    @Inject
    private CreditCardAccountService creditCardAccountService;


    /**
     * Lists all account (CKA) registered for this user, but its stocks aren't loaded.
     *
     * @return List Accounts without its stocks
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Account> listAll() {
        return creditCardAccountService.getAllActiveAccounts(getGroupId());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public Account create(@RequestBody CreditCardAccount creditCardAccount) {

        creditCardAccount.setGroupId(getGroupId());
        creditCardAccount.setActivated(true);
        creditCardAccount.setLastUpdate(Calendar.getInstance().getTime());

        validate(OBJECT_NAME, creditCardAccount);
        return creditCardAccountService.saveAccount(creditCardAccount, getGroupId());
    }

    /**
     * Returns the informed account with no entry.
     * @param creditCardAccount Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccount save(@RequestBody CreditCardAccount creditCardAccount) throws FormValidationError {
        CheckingAccount account = creditCardAccountService.saveAccount(creditCardAccount, getGroupId());
        return account;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public Account getSummarized(@PathVariable Integer id) throws FormValidationError {
        CheckingAccount account = creditCardAccountService.getAccount(id, getGroupId());
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
        CheckingAccount account = creditCardAccountService.getAccount(id, getGroupId());
        return account;
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry editEntry(@RequestBody CreditCardAccountEntry entry) throws FormValidationError {
        if (!entry.getGroupId().equals(getGroupId())){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return creditCardAccountService.updateEntry(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry createEntry(@RequestBody CreditCardAccountEntry entry) throws FormValidationError {
        entry.setGroupId(getGroupId());

        validate(OBJECT_ENTRY_NAME, entry);
        return creditCardAccountService.updateEntry(entry, getGroupId());
    }

    /**
     * Remove this entry, checking if it belongs to current user first
     * @param id
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeEntry(@PathVariable Integer id) throws FormValidationError {
        CheckingAccountEntry entry = (CheckingAccountEntry) creditCardAccountService.getEntry(id, getGroupId());
        if (entry == null){
            throw new BusinessException("Lançamento não encontrado para usuário corrente.");
        }
        creditCardAccountService.delEntry(id);
    }

}

