package com.softb.savefy.cashflow.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.cashflow.model.ConsolidatedCashFlowYear;
import com.softb.savefy.cashflow.service.CashFlowService;
import com.softb.savefy.utils.AppDate;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;

@RestController("AppConsolidatedCashFlowController")
@RequestMapping("/api/cashFlow")
public class CashFlowController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "Patrimony";

    public static final String OBJECT_ENTRY_NAME = "PatrimonyEntry";

    @Inject
    private CashFlowService cashFlowService;


//    /**
//     * Get the current patrimony
//     * @return
//     */
//    @RequestMapping(method = RequestMethod.GET)
//    @ResponseBody public Patrimony get() {
//        return patrimonyService.load(AppDate.getMonthDate(new Date()), getGroupId());
//    }

    /**
     * Generate a new history for the user patrimony
     * @return
     */
    @RequestMapping(value = "/consolidate", method = RequestMethod.GET)
    @ResponseBody public ConsolidatedCashFlowYear consolidate() {
        return cashFlowService.generate(AppDate.getMonthDate(new Date()), getGroupId());
    }

}

