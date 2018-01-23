package com.softb.savefy.account.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.StockAccount;
import com.softb.savefy.account.model.StockAccountEntry;
import com.softb.savefy.account.model.StockSaleProfit;
import com.softb.savefy.account.service.StockAccountService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.errorhandler.exception.FormValidationError;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;

@RestController("AppStockAccountController")
@RequestMapping("/api/account/STK")
public class StockAccountController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "StockAccount";

    public static final String OBJECT_ENTRY_NAME = "StockAccountEntry";

    @Inject
    private StockAccountService stockAccountService;


    /**
     * Create this new investment into the system
     * @param account
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public StockAccount create(@RequestBody StockAccount account) {

        account.setGroupId(getGroupId());
        account.setActivated(true);
        account.setLastUpdate(Calendar.getInstance().getTime());

        validate(OBJECT_NAME, account);
        account = stockAccountService.saveAccount(account, getGroupId());
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
    @ResponseBody public StockAccount save(@RequestBody StockAccount account) throws FormValidationError {
        validate(OBJECT_NAME, account);
        return stockAccountService.saveAccount(account, getGroupId());
    }

    /**
     * Registra um pagamento de IR para um lucro mensal, proveniente de vendas realizadas no mês acima de 20k
     * @param payment
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}/saleProfit", method = RequestMethod.POST)
    @ResponseBody public StockSaleProfit registerProfitTaxPayment(@RequestBody StockSaleProfit payment) throws FormValidationError {
        return stockAccountService.registerStockProfitTaxPayment(payment, getGroupId());
    }

    /**
     * Registra um pagamento de IR para um lucro mensal, proveniente de vendas realizadas no mês acima de 20k
     * @param payment
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}/saleProfit/{profitId}", method = RequestMethod.DELETE)
    @ResponseBody public void removeTaxPayment(@PathVariable Integer profitId) throws FormValidationError {
        stockAccountService.removeTaxPayment(profitId, getGroupId());
    }

    /**
     * Get this account with no calculation, just the account.
     * @param id
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody public Account get(@PathVariable Integer id) throws FormValidationError {
        return stockAccountService.get(id, getGroupId());
    }

    /**
     * Returns the informed account with no entry.
     * @param id Id of the account
     * @return The Account selected
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{id}/detail", method = RequestMethod.GET)
    @ResponseBody public StockAccount getDetailed(@PathVariable Integer id) throws FormValidationError {
        StockAccount account = stockAccountService.getAccountDetailed(id, getGroupId());
        return account;
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.POST)
    @ResponseBody public StockAccountEntry editEntry(@RequestBody StockAccountEntry entry) throws FormValidationError {
        if (!entry.getGroupId().equals(getGroupId())){
            throw new BusinessException("This entry doesn't belong to the current user");
        }
        return stockAccountService.updateCurrentPosition(entry, getGroupId());
    }

    /**
     * Create an entry
     * @param entry
     * @return
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry", method = RequestMethod.POST)
    @ResponseBody public StockAccountEntry createStock(@RequestBody StockAccountEntry entry) throws FormValidationError {
        entry.setGroupId(getGroupId());
        entry.setLastPrice(entry.getOriginalPrice());

        validate(OBJECT_ENTRY_NAME, entry);
        return stockAccountService.saveEntry(entry, getGroupId());
    }

    /**
     * Remove this entry, checking if it belongs to current user first
     * @param id
     * @throws FormValidationError
     */
    @RequestMapping(value = "/{accountId}/entry/{id}", method = RequestMethod.DELETE)
    @ResponseBody public void removeEntry(@PathVariable Integer id) throws FormValidationError {
        StockAccountEntry entry = (StockAccountEntry) stockAccountService.getEntry(id, getGroupId());
        if (entry == null){
            throw new BusinessException("Ação não encontrada para usuário corrente.");
        }
        stockAccountService.delEntry(entry, getGroupId());
    }
}

