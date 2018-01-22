package com.softb.savefy.account.repository;

import com.softb.savefy.account.model.StockSaleProfit;
import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("AppStockAccountProfitRepository")
public interface StockSaleProfitRepository extends JpaRepository<StockSaleProfit, Integer> {

    @Query("select s from StockSaleProfit s where s.date = :date and s.accountId = :accountId and type = '0' and s.groupId = :groupId")
    StockSaleProfit findProfitByDateAccount(@Param("date") Date date, @Param("accountId") Integer accountId, @Param("groupId") Integer groupId) throws DataAccessException;
}
