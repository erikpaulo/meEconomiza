package com.softb.savefy.account.web;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.service.BenefitsAccountService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.FormValidationError;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

@RestController("AppBenefitsAccountController")
@RequestMapping("/api/account/BFA")
public class BenefitsAccountController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "BenefitsAccount";

    public static final String OBJECT_ENTRY_NAME = "BenefitsAccountEntry";

    @Inject
    private BenefitsAccountService benefitsAccountService;


    /**
     * Lists all account (CKA) registered for this user, but its stocks aren't loaded.
     *
     * @return List Accounts without its stocks
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<Account> listAll() {
        return benefitsAccountService.getAllActiveAccounts(getGroupId());
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public Account create(@RequestBody BenefitsAccount benefitsAccount) {

        benefitsAccount.setGroupId(getGroupId());
        benefitsAccount.setActivated(true);
        benefitsAccount.setLastUpdate(Calendar.getInstance().getTime());

        validate(OBJECT_NAME, benefitsAccount);
        return benefitsAccountService.saveAccount(benefitsAccount, getGroupId());
    }

    /**
     * Returns the informed account with no entry.
     * @param benefitsAccount Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccount save(@RequestBody BenefitsAccount benefitsAccount) throws FormValidationError {
        CheckingAccount account = benefitsAccountService.saveAccount(benefitsAccount, getGroupId());
        return account;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public Account getSummarized(@PathVariable Integer id) throws FormValidationError {
        CheckingAccount account = benefitsAccountService.getAccount(id, getGroupId());
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
        CheckingAccount account = benefitsAccountService.getAccount(id, getGroupId());
        return account;
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry editEntry(@RequestBody BenefitsAccountEntry entry) throws FormValidationError {
        if (!entry.getGroupId().equals(getGroupId())){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return benefitsAccountService.updateEntry(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry", method = RequestMethod.POST)
    @ResponseBody public CheckingAccountEntry createEntry(@RequestBody BenefitsAccountEntry entry) throws FormValidationError {
        entry.setGroupId(getGroupId());

        validate(OBJECT_ENTRY_NAME, entry);
        return benefitsAccountService.updateEntry(entry, getGroupId());
    }

    /**
     * Remove this entry, checking if it belongs to current user first
     * @param id
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeEntry(@PathVariable Integer id) throws FormValidationError {
        CheckingAccountEntry entry = (CheckingAccountEntry) benefitsAccountService.getEntry(id, getGroupId());
        if (entry == null){
            throw new BusinessException("Lançamento não encontrado para usuário corrente.");
        }
        benefitsAccountService.delEntry(id);
    }

}

