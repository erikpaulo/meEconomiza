package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.ConciliationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("AppConciliationEntryRepository")
public interface ConciliationEntryRepository extends JpaRepository<ConciliationEntry, Integer> {
	
}
