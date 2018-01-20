package com.softb.savefy.account.service;

import com.softb.savefy.account.model.InvestmentAccount;
import com.softb.savefy.account.model.InvestmentAccountEntry;
import com.softb.savefy.account.model.StockAccountEntry;
import com.softb.savefy.utils.AppDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class IncomeTaxRateService {

    public static final int DAYS_OF_YEAR = 360;

    public IncomeTaxRateService.TaxRange getTaxRange(StockAccountEntry stock, Boolean isDayTrade){
        Double percent = 0.0;
        if (isDayTrade){
            percent = 0.20;
        } else {
            percent = 0.15;
        }
        return new IncomeTaxRateService.TaxRange(percent, new Date());
    }

    public IncomeTaxRateService.TaxRange getTaxRange(InvestmentAccount account, InvestmentAccountEntry entry){
        IncomeTaxRateService.TaxRange range = new IncomeTaxRateService.TaxRange(0.0, new Date());

        Long age = getAge(entry);
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getDate());

        Double taxRange = 0.0;
        if (account.getProduct().equals(InvestmentAccount.Product.FIXED_INCOME) || account.getProduct().equals(InvestmentAccount.Product.MULTI_SHARES)){
            if (age>=0 && age<=180) {
                range.taxRange = 0.225;
                cal.add(Calendar.DAY_OF_YEAR,180);
            } else if (age>=181 && age<=360) {
                range.taxRange = 0.200;
                cal.add(Calendar.DAY_OF_YEAR,360);
            } else if (age>=361 && age<=720) {
                range.taxRange = 0.175;
                cal.add(Calendar.DAY_OF_YEAR,720);
            } else if (age>720) {
                range.taxRange = 0.150;
                cal = null;
            }
        } else if (account.getProduct().equals(InvestmentAccount.Product.PENSION_FUND)){
            if (age>=0 && age<=720) {
                range.taxRange = 0.350;
                cal.add(Calendar.DAY_OF_YEAR,720);
            } else if (age>=721 && age<=1440) {
                range. taxRange = 0.300;
                cal.add(Calendar.DAY_OF_YEAR,1440);
            } else if (age>=1441 && age<=2160) {
                range.taxRange = 0.250;
                cal.add(Calendar.DAY_OF_YEAR,2160);
            } else if (age>=2161 && age<=2880) {
                range.taxRange = 0.200;
                cal.add(Calendar.DAY_OF_YEAR,2880);
            } else if (age>=2881 && age<=3600) {
                range.taxRange = 0.150;
                cal.add(Calendar.DAY_OF_YEAR,3600);
            } else if (age>3600) {
                range.taxRange = 0.100;
                cal = null;
            }
        } else if (account.getProduct().equals(InvestmentAccount.Product.FUND_OF_SHARES)){
            range.taxRange = 0.150;
            cal = null;
        }
        range.nextRangeDate = (cal != null ? cal.getTime() : null);

        return range;
    }

    private Double getMaxTaxRange(InvestmentAccount account){
        Double taxRange = 0.0;
        if (account.getProduct().equals(InvestmentAccount.Product.FIXED_INCOME) || account.getProduct().equals(InvestmentAccount.Product.MULTI_SHARES)){
            taxRange = 0.150;
        } else if (account.getProduct().equals(InvestmentAccount.Product.PENSION_FUND)){
            taxRange = 0.100;
        } else if (account.getProduct().equals(InvestmentAccount.Product.FUND_OF_SHARES)){
            taxRange = 0.150;
        }

        return taxRange;

    }

    private Long getAge(InvestmentAccountEntry entry) {
        Date curDate = new Date();
        return AppDate.getDifferenceDays(entry.getDate(), curDate);
    }

    @AllArgsConstructor
    private class TaxRange{
        Double taxRange;
        Date nextRangeDate;
    }

}
