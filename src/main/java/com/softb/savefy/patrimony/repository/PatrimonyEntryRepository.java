package com.softb.savefy.patrimony.repository;

import com.softb.savefy.patrimony.model.PatrimonyEntry;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("AppAPatrimonyEntryRepository")
public interface PatrimonyEntryRepository extends JpaRepository<PatrimonyEntry, Integer> {

    @Query("select pe from PatrimonyEntry pe where pe.date = :date and pe.accountId = :accountId and pe.groupId = :groupId")
    PatrimonyEntry findByDateAccount(@Param("date") Date date, @Param("accountId") Integer accountId, @Param("groupId") Integer groupId) throws DataAccessException;

    @Query("select pe from PatrimonyEntry pe where pe.accountId = :accountId and pe.groupId = :groupId")
    List<PatrimonyEntry> findByAccount(@Param("accountId") Integer accountId, @Param("groupId") Integer groupId) throws DataAccessException;
}
