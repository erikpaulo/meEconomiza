package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.StockSaleProfit;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository("AppStockAccountProfitRepository")
public interface StockSaleProfitRepository extends JpaRepository<StockSaleProfit, Integer> {

    @Query("select s from StockSaleProfit s where s.date = :date and type = '0' and s.groupId = :groupId")
    StockSaleProfit findProfitByDateUser(@Param("date") Date date, @Param("groupId") Integer groupId) throws DataAccessException;

    @Query("select s from StockSaleProfit s where s.groupId = :groupId")
    List<StockSaleProfit> findAllByUser(@Param("groupId") Integer groupId) throws DataAccessException;

    @Query("select s from StockSaleProfit s where s.id = :id and s.groupId = :groupId")
    StockSaleProfit findOne(@Param("id") Integer id, @Param("groupId") Integer groupId) throws DataAccessException;
}
