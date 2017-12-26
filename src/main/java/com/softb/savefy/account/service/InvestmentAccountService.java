package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.IndexRepository;
import com.softb.savefy.account.repository.QuoteSaleRepository;
import com.softb.savefy.utils.AppDate;
import com.softb.savefy.utils.AppMaths;
import com.softb.system.errorhandler.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class InvestmentAccountService extends AbstractAccountService {

    public static final int DAYS_OF_YEAR = 360;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IndexRepository indexRepository;

    @Autowired
    private QuoteSaleRepository quoteSaleRepository;

    /**
     * Save a new Investment into the system
     * @param account
     * @param groupId
     * @return
     */
    public InvestmentAccount saveAccount(InvestmentAccount account, Integer groupId){
        account = accountRepository.save(account);
        return account;
    }

    /**
     * Return the account with all its dependencies;
     * @param id
     * @param groupId
     * @return
     */
    public InvestmentAccount getAccountDetailed(Integer id, Integer groupId){
        InvestmentAccount account = (InvestmentAccount)get(id, groupId);
        calcAccountBalance(account);
        return account;
    }

    /**
     * Calulate the account balance, considering the current value of its entries.
     * @param account
     */
    @Override
    public void calcAccountBalance(Account account) {
        InvestmentAccount investmentAccount = (InvestmentAccount) account;

        Double balance = 0.0;
        for (InvestmentAccountEntry entry: ((InvestmentAccount) account).getEntries()) {
            if (entry.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){
                calcPrevisionGains(investmentAccount, entry);
                balance += entry.getCurrentAmount();
            }
        }
        account.setBalance(balance);
    }

    /**
     * Calculate all calculated field of an investment entry, considering its age and last index value.
     * @param account
     * @param entry
     */
    private void calcPrevisionGains(InvestmentAccount account, InvestmentAccountEntry entry){
        TaxRange taxRange = getTaxRange(account, entry);

        Index currentIndex = getLastIndex(account, account.getGroupId());
        entry.setCurrentAmount(entry.getQuotesAvailable() * currentIndex.getValue());

        Double currentOriginalValue = (entry.getQuotesAvailable()*entry.getQuoteValue());

        entry.setIncomeTaxPercent(taxRange.taxRange);
        entry.setNextTaxRangeDate(taxRange.nextRangeDate);
        entry.setGrossProfitability((entry.getQuotesAvailable()*currentIndex.getValue()) - currentOriginalValue);

        entry.setIncomeTaxAmount(entry.getGrossProfitability() * (entry.getIncomeTaxPercent()));
        entry.setNetProfitability(entry.getGrossProfitability() - entry.getIncomeTaxAmount());

        entry.setPercentGrossProfitability(entry.getGrossProfitability() / currentOriginalValue);
        entry.setPercentNetProfitability(entry.getNetProfitability() / currentOriginalValue);
    }

    /**
     * Save an entry, checking if it belongs to current user.
     * @param entry
     * @return
     */
    public InvestmentAccountEntry saveEntry(InvestmentAccountEntry entry, Integer groupId){
        if (entry.getId() == null){
            indexRepository.save(new Index(entry.getAccountId(), entry.getDate(), entry.getQuoteValue(), groupId));
        }
        InvestmentAccount account = (InvestmentAccount) accountRepository.findOne(entry.getAccountId(), groupId);

        if (entry.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){
            entry = savePurchaseEntry(account, entry, groupId);
        } else if (entry.getOperation().equals(InvestmentAccountEntry.Operation.SALE)) {
            entry = saveSaleEntry(account, entry, groupId);
        } else if (entry.getOperation().equals(InvestmentAccountEntry.Operation.IR_LAW)){
            entry = saveIRLawEntry(account, entry, groupId);
        }

        return entry;
    }

    private Index getLastIndex(InvestmentAccount account, Integer groupId){
        Index index = null;

        // Sort Conciliations DESC
        Collections.sort(account.getIndexValues(), new Comparator<Index>(){
            public int compare(Index o1, Index o2) {
                return o2.getDate().compareTo(o1.getDate());
            }
        });

        List<Index> indexValues = account.getIndexValues();
        if (indexValues==null || indexValues.size() <= 0){
            index = new Index(account.getId(), new Date(), 0.0, groupId);
        } else {
            index = indexValues.get(0);
        }

        return index;
    }

    private InvestmentAccountEntry saveIRLawEntry(InvestmentAccount account, InvestmentAccountEntry entry, Integer groupId){
        entry.setIncomeTaxAmount(entry.getAmount());
        InvestmentAccountEntry irEntry = saveSaleEntry(account, entry, groupId);

        irEntry.setIncomeTaxAmount(entry.getCurrentAmount());
        irEntry.setIncomeTaxPercent(getMaxTaxRange(account));

        return (InvestmentAccountEntry) save(irEntry, groupId);
    }

    private InvestmentAccountEntry savePurchaseEntry(InvestmentAccount account, InvestmentAccountEntry entry, Integer groupId){
        InvestmentAccountEntry savedEntry = (InvestmentAccountEntry) save(entry, groupId);
        return savedEntry;
    }

    private InvestmentAccountEntry saveSaleEntry(InvestmentAccount account, InvestmentAccountEntry entry, Integer groupId){
        List<InvestmentAccountEntry> entries = account.getEntries();

        // Sort Conciliations DESC
        Collections.sort(entries, new Comparator<InvestmentAccountEntry>(){
            public int compare(InvestmentAccountEntry o1, InvestmentAccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        entry = (InvestmentAccountEntry) save(entry, groupId);

        Double qtdQuotesToSell = AppMaths.round(entry.getAmount() / entry.getQuoteValue(), 8);

        Double originalAmount=0.0, grossProfit=0.0, percentGrossProfit=0.0, netProfit=0.0, percentNetProfit=0.0;
        Double incomeTaxAmount=0.0, qtdQuotesForCalc=0.0;
        Double incomeTaxPercent = getTaxRange(account, entry).taxRange;
        int i=0; Boolean done=false; Double quoteDiff=0.0;
        while(i<entries.size() && !done){
            InvestmentAccountEntry entryToSell = entries.get(i);

            if (entryToSell.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){

                quoteDiff = AppMaths.round(entryToSell.getQuotesAvailable() - qtdQuotesToSell,8);
                if (quoteDiff >= 0){
                    entryToSell.setQuotesAvailable(AppMaths.round(entryToSell.getQuotesAvailable() - qtdQuotesToSell,8));
                    entryToSell.setAmount(entryToSell.getQuotesAvailable() * entryToSell.getQuoteValue());

                    quoteSaleRepository.save(new QuoteSale(entryToSell, entry, qtdQuotesToSell));

                    save(entryToSell, groupId);

                    qtdQuotesForCalc = qtdQuotesToSell;
                    done = true;
                } else {
                    if (quoteDiff.equals(0.00000001) || quoteDiff.equals(-0.00000001)){
                        done=true;
                        qtdQuotesToSell = entryToSell.getQuotesAvailable();
                        qtdQuotesForCalc = qtdQuotesToSell;
                    } else {
                        qtdQuotesForCalc = entryToSell.getQuotesAvailable();
                    }
                    quoteSaleRepository.save(new QuoteSale(entryToSell, entry, entryToSell.getQuotesAvailable()));

                    entryToSell.setQuotesAvailable(0.0);
                    entryToSell.setAmount(0.0);
                    save(entryToSell, groupId);

                }

                // Calc gains
                originalAmount += (entryToSell.getQuoteValue() * qtdQuotesForCalc);
                grossProfit += (qtdQuotesForCalc * entry.getQuoteValue()) - (entryToSell.getQuoteValue() * qtdQuotesForCalc);
                incomeTaxPercent = (getTaxRange(account, entryToSell).taxRange > incomeTaxPercent ? getTaxRange(account, entryToSell).taxRange : incomeTaxPercent);
                netProfit += grossProfit - entry.getIncomeTaxAmount();

                qtdQuotesToSell = AppMaths.round(qtdQuotesToSell - entryToSell.getQuotesAvailable(),8);
            }
            i++;
        }

        if (i==entries.size()+1){
            throw new BusinessException("Insufficient Money");
        }

        percentGrossProfit = grossProfit / originalAmount * 100;
        percentNetProfit = netProfit / originalAmount * 100;

        entry.setGrossProfitability(grossProfit);
        entry.setNetProfitability(netProfit);
        entry.setPercentGrossProfitability(percentGrossProfit);
        entry.setIncomeTaxPercent(incomeTaxPercent);
        entry.setPercentNetProfitability(percentNetProfit);
        entry.setCurrentAmount(entry.getAmount());
        entry.setAmount(originalAmount);
        entry = (InvestmentAccountEntry) save(entry, groupId);

        return entry;
    }

    private TaxRange getTaxRange(InvestmentAccount account, InvestmentAccountEntry entry){
        TaxRange range = new TaxRange(0.0, new Date());

        Long age = getAge(entry);
        Calendar cal = Calendar.getInstance();
        cal.setTime(entry.getDate());

        Double taxRange = 0.0;
        if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND)){
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
        } else if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND_PENSION)){
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
        } else if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND_OF_SHARES)){
            range.taxRange = 0.150;
            cal = null;
        }
        range.nextRangeDate = (cal != null ? cal.getTime() : null);

        return range;
    }

    private Double getMaxTaxRange(InvestmentAccount account){
        Double taxRange = 0.0;
        if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND)){
            taxRange = 0.150;
        } else if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND_PENSION)){
            taxRange = 0.100;
        } else if (account.getProduct().equals(InvestmentAccount.Product.INVESTMENT_FUND_OF_SHARES)){
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
