package com.softb.savefy.patrimony.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.patrimony.model.Patrimony;
import com.softb.savefy.patrimony.service.PatrimonyService;
import com.softb.savefy.utils.AppDate;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

@RestController("AppPatrimonyController")
@RequestMapping("/api/patrimony")
public class PatrimonyController extends AbstractRestController<Account, Integer> {

    public static final String OBJECT_NAME = "Patrimony";

    public static final String OBJECT_ENTRY_NAME = "PatrimonyEntry";

    @Inject
    private PatrimonyService patrimonyService;


    /**
     * Get the current patrimony
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody public Patrimony get() {
        return patrimonyService.load(AppDate.getMonthDate(new Date()), getGroupId());
    }

    /**
     * Generate a new history for the user patrimony
     * @return
     */
    @RequestMapping(value = "/baseline", method = RequestMethod.POST)
    @ResponseBody public Patrimony baseline(@RequestBody Patrimony patrimony) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(patrimony.getDate());
        cal.set(Calendar.DAY_OF_MONTH, 01);

        patrimony.setDate(cal.getTime());
        return patrimonyService.baseline(patrimony, getGroupId());
    }

}

