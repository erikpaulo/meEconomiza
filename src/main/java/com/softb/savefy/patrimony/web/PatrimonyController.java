package com.softb.savefy.patrimony.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.patrimony.model.Patrimony;
import com.softb.savefy.patrimony.service.PatrimonyService;
import com.softb.system.rest.AbstractRestController;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Calendar;

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
        Calendar cal1 = Calendar.getInstance();
        cal1.set(Calendar.AM_PM, Calendar.AM);
        cal1.set(Calendar.HOUR, 2);
        cal1.set(Calendar.MINUTE, 0);
        cal1.set(Calendar.SECOND, 0);
        cal1.set(Calendar.MILLISECOND, 0);
        return patrimonyService.load(cal1.getTime(), getGroupId());
    }

    /**
     * Generate a new history for the user patrimony
     * @return
     */
    @RequestMapping(value = "/baseline", method = RequestMethod.POST)
    @ResponseBody public Patrimony baseline(@RequestBody Patrimony patrimony) {
        return patrimonyService.baseline(patrimony, getGroupId());
    }

}

