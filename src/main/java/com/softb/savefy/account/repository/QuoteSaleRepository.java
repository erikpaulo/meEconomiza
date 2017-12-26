package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.QuoteSale;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("AppQuoteSaleRepository")
public interface QuoteSaleRepository extends JpaRepository<QuoteSale, Integer> {

    @Query("select qs from QuoteSale qs where qs.saleEntry.id = :id")
    List<QuoteSale> findAllBySaleId(@Param("id") Integer id) throws DataAccessException;
}
