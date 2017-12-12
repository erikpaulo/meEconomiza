package com.softb.savefy.account.web;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.Conciliation;
import com.softb.savefy.account.service.AccountsService;
import com.softb.savefy.account.service.ConciliationService;
import com.softb.savefy.preferences.services.UserPreferencesService;
import com.softb.system.errorhandler.exception.SystemException;
import com.softb.system.rest.AbstractRestController;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;

@RestController("AppConciliationController")
@RequestMapping("/api/account/{id}/conciliation")
public class ConciliationController extends AbstractRestController<Account, Integer> {

    public static final String CHECKING_ACCOUNT_OBJECT_NAME = "CheckingAccount";

    @Inject
    private AccountsService accountService;

    @Inject
    private ConciliationService conciliationService;

    @Inject
    private UserPreferencesService userPreferencesService;

    /**
     * Inactivate this account
     * @param id
     */
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable Integer id) {
        accountService.inactivate(id, getGroupId());
    }

    /**
     * Import account entries from a CSV file. This point prepares the file's data to be complemented by the user.
     * @param id Id of the Account
     * @param request Request
     * @param response Response
     * @return
     * @throws SystemException
     * @throws DataAccessException
     * @throws FileUploadException
     * @throws IOException
     * @throws ParseException
     */
    @RequestMapping(value="/upload", method = RequestMethod.POST)
    @ResponseBody public Conciliation uploadEntries(@PathVariable Integer id,
                                                    final HttpServletRequest request,
                                                    final HttpServletResponse response  )
            throws SystemException, DataAccessException, FileUploadException, IOException, ParseException {

        // Treat file data, creating a conciliation structure
        Conciliation conciliation = conciliationService.uploadEntries(id, request, response, getGroupId());

        // Save the draft and return.
        return conciliationService.save(conciliation, getGroupId());
    }

    @RequestMapping(value="/{conciliationId}", method = RequestMethod.GET)
    @ResponseBody public Conciliation getConciliation( @PathVariable Integer conciliationId  ){
        return conciliationService.get(conciliationId, getGroupId(), true);
    }

    @RequestMapping(value="/{conciliationId}", method = RequestMethod.POST)
    @ResponseBody public Conciliation saveConciliation( @RequestBody Conciliation conciliation  ){

        return conciliationService.save(conciliation, getGroupId());
    }

    @RequestMapping(value="/{conciliationId}", method = RequestMethod.DELETE)
    @ResponseBody public void delConciliation( @PathVariable Integer id, @PathVariable Integer conciliationId ){
        conciliationService.delete(conciliationId, getGroupId());
    }

    @RequestMapping(value="/{conciliationId}/sync", method = RequestMethod.POST)
    @ResponseBody public Conciliation syncConciliationIntoAccount( @PathVariable Integer id, @RequestBody Conciliation conciliation  ){
        return conciliationService.syncEntriesIntoAccount(conciliation, getGroupId());
    }

    @RequestMapping(value="/{conciliationId}/rollback", method = RequestMethod.POST)
    @ResponseBody public Conciliation rollbackConciliation( @PathVariable Integer conciliationId  ){
        return conciliationService.rollback(conciliationId, getGroupId());
    }


}

