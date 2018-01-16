package com.softb.savefy.patrimony.repository;

import com.softb.savefy.patrimony.model.Patrimony;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("AppAPatrimonyRepository")
public interface PatrimonyRepository extends JpaRepository<Patrimony, Integer> {

    @Query("select p from Patrimony p where p.date = :date and p.groupId = :groupId order by p.date desc")
    Patrimony findByDate(@Param("date") Date date, @Param("groupId") Integer groupId) throws DataAccessException;

    @Query("select p from Patrimony p where p.groupId = :groupId order by p.date desc")
    List<Patrimony> findAll(@Param("groupId") Integer groupId) throws DataAccessException;
}
