package com.softb.meeconomiza.categorization.repository;

import com.softb.meeconomiza.categorization.model.CategoryPrediction;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("AppCategoryPredictionRepository")
public interface CategoryPredictionRepository extends JpaRepository<CategoryPrediction, Integer> {
	
	@Query("select cp from CategoryPrediction cp where cp.description like :description and cp.groupId = :groupId")
    CategoryPrediction getByDescription(@Param("description") String description, @Param("groupId") Integer groupId) throws DataAccessException;
}
