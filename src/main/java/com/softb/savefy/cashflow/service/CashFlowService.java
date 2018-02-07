package com.softb.savefy.cashflow.service;

import com.softb.savefy.account.model.CheckingAccountEntry;
import com.softb.savefy.account.service.AccountsService;
import com.softb.savefy.account.service.CheckingAccountService;
import com.softb.savefy.cashflow.model.ConsolidatedCashFlowYear;
import com.softb.savefy.categorization.model.Category;
import com.softb.savefy.utils.AppMaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class CashFlowService {

    @Autowired
    protected AccountsService accountsService;

    @Autowired
    protected CheckingAccountService checkingAccountService;

    public ConsolidatedCashFlowYear generate(Date date, Integer groupId){
        Calendar monthStart = Calendar.getInstance();
        Calendar monthEnd = Calendar.getInstance();

        monthStart.setTime(date);
        monthEnd.setTime(date);

        monthStart.set(Calendar.DAY_OF_MONTH, 01);
        monthStart.set(Calendar.MONTH, 0);
        monthEnd.set(Calendar.DAY_OF_MONTH, monthEnd.getActualMaximum(Calendar.DAY_OF_MONTH));

        ConsolidatedCashFlowYear year = new ConsolidatedCashFlowYear(monthStart.get(Calendar.YEAR));

        List<CheckingAccountEntry> entries = checkingAccountService.getAllBetween(monthStart.getTime(), monthEnd.getTime(), groupId);
        for (CheckingAccountEntry entry: entries) {
            if (entry.getSubCategory().getCategory().getType().equals(Category.Type.INC)){
                add(year.getPerMonthIncome(), entry);
            } else if (entry.getSubCategory().getCategory().getType().equals(Category.Type.EXP)) {
                add(year.getPerMonthExpense(), entry);
            }
        }

        Double incVariation, incAverage, expVariation, expAverage;
        Double income, expense, totalIncome=0.0, totalExpense=0.0, prevIncome=0.0, prevExpense=0.0;
        int incCount=0, expCount=0;
        for (int i=0;i<12;i++){
            income = year.getPerMonthIncome().get(i);
            totalIncome += income;
            incAverage = (income!=0? AppMaths.round(totalIncome / (i+1), 2): 0.0);
            incVariation = (prevIncome!=0 ? AppMaths.round((income - prevIncome)/prevIncome * 100, 2) : 0.0);
            prevIncome = income;

            expense = year.getPerMonthExpense().get(i) *-1;
            totalExpense += expense;
            expAverage = (expense!=0? AppMaths.round(totalExpense / (i+1), 2): 0.0);
            expVariation = (prevExpense!=0 ? AppMaths.round((expense - prevExpense)/prevExpense * 100, 2) : 0.0);
            prevExpense = expense;

            year.getPerMonthIncAverage().set(i, incAverage);
            year.getPerMonthIncVariation().set(i, incVariation);

            year.getPerMonthExpAverage().set(i, expAverage);
            year.getPerMonthExpVariation().set(i, expVariation);
        }

        return year;
    }

    private Integer getIndexFromMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    private void add(List<Double> arr, CheckingAccountEntry entry){
        Integer month = getIndexFromMonth(entry.getDate());

        Double value = arr.get(month);
        value += entry.getAmount();

        arr.set(month, value);
    }
}
