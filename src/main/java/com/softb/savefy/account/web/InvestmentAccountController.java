package com.softb.savefy.account.web;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.service.IndexService;
import com.softb.savefy.account.service.InvestmentAccountService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.FormValidationError;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;

@RestController("AppInvestmentAccountController")
@RequestMapping("/api/account/INV")
public class InvestmentAccountController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "InvestmentAccount";

    public static final String OBJECT_ENTRY_NAME = "InvestmentAccountEntry";

    public static final String OBJECT_INDEX_NAME = "Index";

    @Inject
    private InvestmentAccountService investmentAccountService;

    @Inject
    private IndexService indexService;


    /**
     * Create this new investment into the system
     * @param account
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public InvestmentAccount create(@RequestBody InvestmentAccount account) {

        account.setGroupId(getGroupId());
        account.setActivated(true);
        account.setLastUpdate(Calendar.getInstance().getTime());

        validate(OBJECT_NAME, account);
        account = investmentAccountService.saveAccount(account, getGroupId());
        account.setBalance(0.0);
        return account;
    }

    /**
     * Save this investment
     * @param account Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody public InvestmentAccount save(@RequestBody InvestmentAccount account) throws FormValidationError {
        validate(OBJECT_NAME, account);
        return investmentAccountService.saveAccount(account, getGroupId());
    }

    /**
     * Get this account with no calculation, just the account.
     * @param id
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public Account get(@PathVariable Integer id) throws FormValidationError {
        return investmentAccountService.get(id, getGroupId());
    }

    /**
     * Returns the informed account with no entry.
     * @param id Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
    @ResponseBody public InvestmentAccount getDetailed(@PathVariable Integer id) throws FormValidationError {
        InvestmentAccount account = investmentAccountService.getAccountDetailed(id, getGroupId());
        return account;
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.POST)
    @ResponseBody public InvestmentAccountEntry editEntry(@RequestBody InvestmentAccountEntry entry) throws FormValidationError {
        if (!entry.getGroupId().equals(getGroupId())){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return investmentAccountService.saveEntry(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry", method = RequestMethod.POST)
    @ResponseBody public InvestmentAccountEntry createEntry(@RequestBody InvestmentAccountEntry entry) throws FormValidationError {
        entry.setGroupId(getGroupId());
        entry.setQuotes(entry.getAmount() / entry.getQuoteValue());
        entry.setQuotesAvailable(entry.getQuotes());

        validate(OBJECT_ENTRY_NAME, entry);
        return investmentAccountService.saveEntry(entry, getGroupId());
    }

    /**
     * Remove this entry, checking if it belongs to current user first
     * @param id
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeEntry(@PathVariable Integer id) throws FormValidationError {
        InvestmentAccountEntry entry = (InvestmentAccountEntry) investmentAccountService.getEntry(id, getGroupId());
        if (entry == null){
            throw new BusinessException("Lançamento não encontrado para usuário corrente.");
        }
        investmentAccountService.delEntry(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param index
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/index", method = RequestMethod.POST)
    @ResponseBody public Index createIndexValue(@RequestBody Index index) throws FormValidationError {
        index.setGroupId(getGroupId());

        validate(OBJECT_INDEX_NAME, index);
        return indexService.save(index, getGroupId());
    }

    /**
     * Remove this entry, checking if it belongs to current user first
     * @param id
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/index/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeIndexValue(@PathVariable Integer id) throws FormValidationError {
        indexService.delIndexValue(id, getGroupId());
    }

}

