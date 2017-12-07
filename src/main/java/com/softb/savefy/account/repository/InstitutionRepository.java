package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.Institution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("AppInstitutionRepository")
public interface InstitutionRepository extends JpaRepository<Institution, Integer> {
	
}
