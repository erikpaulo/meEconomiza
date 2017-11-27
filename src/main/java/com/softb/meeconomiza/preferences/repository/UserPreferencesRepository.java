package com.softb.meeconomiza.preferences.repository;

import com.softb.meeconomiza.preferences.model.UserPreferences;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("AppUserPreferencesRepository")
public interface UserPreferencesRepository extends JpaRepository<UserPreferences, Integer> {

    @Query("select up from UserPreferences up where up.groupId = :groupId")
    UserPreferences findOneByGroup(@Param("groupId") Integer groupId) throws DataAccessException;
}
