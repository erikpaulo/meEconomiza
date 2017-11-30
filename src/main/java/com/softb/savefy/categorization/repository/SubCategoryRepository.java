package com.softb.savefy.categorization.repository;

import com.softb.savefy.categorization.model.SubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("AppSubCategoryRepository")
public interface SubCategoryRepository extends JpaRepository<SubCategory, Integer> {

//    @Query("select s from SubCategory s where s.id = :id and s.groupId = :groupId")
//    SubCategory findOneByUser(@Param("id") Integer id, @Param("groupId") Integer groupId) throws DataAccessException;

}
