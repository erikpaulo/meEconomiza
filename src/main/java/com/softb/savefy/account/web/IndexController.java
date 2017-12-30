package com.softb.savefy.account.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.AssetPrice;
import com.softb.savefy.account.service.IndexService;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController("AppIndexController")
@RequestMapping("/api/account/{accountType}/{accountId}/index")
public class IndexController extends AbstractRestController<Account, Integer> {

    @Inject
    private IndexService indexService;

    /**
     * Inactivate this account
     * @param index
     */
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void delete(@RequestBody AssetPrice index) {
        indexService.delete(index, getGroupId());
    }

    /**
     * Get all by Investment
     * @param accountId
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody public List<AssetPrice> listAllByInvestment(@PathVariable Integer accountId) {
        return indexService.getAll(accountId);
    }

    /**
     * Create a new index value for this investment
     * @param index
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody public AssetPrice create(@RequestBody AssetPrice index) {
        return indexService.save(index, getGroupId());
    }
}

