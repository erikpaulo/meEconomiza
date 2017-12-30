package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.AssetPrice;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("AppIndexRepository")
public interface AssetPriceRepository extends JpaRepository<AssetPrice, Integer> {

    @Query("select i from AssetPrice i where i.accountId = :id order by i.date DESC")
    List<AssetPrice> findAllByInvestment(@Param("id") Integer id) throws DataAccessException;
}
