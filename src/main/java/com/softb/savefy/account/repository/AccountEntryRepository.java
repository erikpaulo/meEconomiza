package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.AccountEntry;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("AppAccountEntryRepository")
public interface AccountEntryRepository extends JpaRepository<AccountEntry, Integer> {

    @Query("select ae from AccountEntry ae where ae.id = :id and ae.groupId = :groupId")
    AccountEntry findOne(@Param("id") Integer id, @Param("groupId") Integer groupId) throws DataAccessException;

    @Query("select ae from AccountEntry ae where ae.groupId = :groupId and ae.accountId = :accountId and ae.date = :date and ae.amount = :amount")
    List<AccountEntry> listAllByDateAmount(@Param("groupId") Integer groupId, @Param("accountId") Integer accountId, @Param("date") Date date, @Param("amount") Double amount) throws DataAccessException;

}
