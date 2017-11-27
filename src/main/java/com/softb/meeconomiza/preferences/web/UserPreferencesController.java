package com.softb.meeconomiza.preferences.web;

import com.softb.meeconomiza.categorization.model.Category;
import com.softb.meeconomiza.preferences.model.UserPreferences;
import com.softb.meeconomiza.preferences.repository.UserPreferencesRepository;
import com.softb.meeconomiza.preferences.services.UserPreferencesService;
import com.softb.system.errorhandler.exception.BusinessException;
import com.softb.system.rest.AbstractRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;

@RestController("AppUserPreferencesController")
@RequestMapping("/api/user")
public class UserPreferencesController extends AbstractRestController<Category, Integer> {

	@Autowired
	private UserPreferencesRepository userPreferencesRepository;


    @Inject
    private UserPreferencesService userPreferencesService;

    @RequestMapping(value = "/preferences", method = RequestMethod.GET)
    public UserPreferences get() {
        return userPreferencesService.get(getGroupId());
    }

    @RequestMapping(value = "/preferences/{id}", method = RequestMethod.POST)
    public UserPreferences update(@RequestBody UserPreferences preferences) {

        UserPreferences up = userPreferencesRepository.findOneByGroup(getGroupId());
        if (up == null) {
            throw new BusinessException("Preferências não pertencem ao usuário corrente.");
        }

        userPreferencesRepository.save(preferences);
       return preferences;
    }

}

