package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.Conciliation;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("AppConciliationRepository")
public interface ConciliationRepository extends JpaRepository<Conciliation, Integer> {
	
	@Query("select c from Conciliation c where c.groupId = :groupId and c.accountId = :accountId order by c.date")
	List<Conciliation> findAllByUser(@Param("accountId") Integer accountId, @Param("groupId") Integer groupId) throws DataAccessException;

	@Query("select c from Conciliation c where c.groupId = :groupId and c.id = :id order by c.date")
	Conciliation findOne(@Param("id") Integer id, @Param("groupId") Integer groupId) throws DataAccessException;
}
