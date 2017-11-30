package com.softb.savefy.categorization.repository;

import com.softb.savefy.categorization.model.SanitizePattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("AppSanitizePatternRepository")
public interface SanitizePatternRepository extends JpaRepository<SanitizePattern, Integer> {
	
}
