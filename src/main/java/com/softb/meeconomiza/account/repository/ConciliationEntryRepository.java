package com.softb.meeconomiza.account.repository;

import com.softb.meeconomiza.account.model.ConciliationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("AppConciliationEntryRepository")
public interface ConciliationEntryRepository extends JpaRepository<ConciliationEntry, Integer> {
	
}
