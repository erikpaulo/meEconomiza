package com.softb.savefy.account.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.InvestmentAccount;
import com.softb.savefy.account.model.InvestmentAccountEntry;
import com.softb.savefy.account.model.QuoteSale;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.QuoteSaleRepository;
import com.softb.savefy.account.web.resource.AssetPrice;
import com.softb.savefy.utils.AppMaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class InvestmentAccountService extends AbstractAccountService {

    public static final int DAYS_OF_YEAR = 360;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private QuoteSaleRepository quoteSaleRepository;

    @Inject
    private IncomeTaxRateService incomeTaxRateService;

    /**
     * Save a new Investment into the system
     * @param account
     * @param groupId
     * @return
     */
    public InvestmentAccount saveAccount(InvestmentAccount account, Integer groupId){
        account = accountRepository.save(account);
        calcAccountBalance(account);
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
     * Calulate the account balance, considering the current profit of its stocks.
     * @param account
     */
    @Override
    public void calcAccountBalance(Account account) {
        InvestmentAccount investmentAccount = (InvestmentAccount) account;

        if (investmentAccount.getEntries() != null){
            // Sort Conciliations DESC
            Collections.sort(investmentAccount.getEntries(), new Comparator<InvestmentAccountEntry>(){
                public int compare(InvestmentAccountEntry o1, InvestmentAccountEntry o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            Double balance = 0.0, grossBalance = 0.0, grossProfit = 0.0, netProfit = 0.0, amountInvested = 0.0;
            for (InvestmentAccountEntry entry: investmentAccount.getEntries()) {
                if (entry.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){
                    calcPrevisionGains(investmentAccount, entry);
                    balance += (entry.getCurrentAmount() - entry.getIncomeTaxAmount());
                    grossBalance += entry.getCurrentAmount();

                    grossProfit += entry.getGrossProfitability();
                    netProfit += entry.getNetProfitability();
                    amountInvested += entry.getAmount();
                }
            }
            }
        investmentAccount.setBalance(balance);
        investmentAccount.setGrossBalance(grossBalance);

        investmentAccount.setGrossProfit(grossProfit);
        investmentAccount.setNetProfit(netProfit);
        investmentAccount.setPercentGrossProfit(grossProfit / amountInvested * 100);
        investmentAccount.setPercentNetProfit(netProfit / amountInvested * 100);
    }

    /**
     * Calculate all calculated field of an investment entry, considering its age and last index profit.
     * @param account
     * @param entry
     */
    private void calcPrevisionGains(InvestmentAccount account, InvestmentAccountEntry entry){
        IncomeTaxRateService.TaxRange taxRange = getTaxRange(account, entry);

//        AssetPrice currentIndex = getLastIndex(account, account.getGroupId());
        entry.setCurrentAmount(entry.getQuotesAvailable() * entry.getQuoteLastValue());

        Double currentOriginalValue = (entry.getQuotesAvailable()*entry.getQuoteValue());

        entry.setIncomeTaxPercent(taxRange.taxRange);
        entry.setNextTaxRangeDate(taxRange.nextRangeDate);
        entry.setGrossProfitability(entry.getCurrentAmount() - currentOriginalValue);

        entry.setIncomeTaxAmount(entry.getGrossProfitability() * (entry.getIncomeTaxPercent()));
        entry.setNetProfitability(entry.getGrossProfitability() - entry.getIncomeTaxAmount());

        entry.setPercentGrossProfitability(entry.getGrossProfitability() / currentOriginalValue);
        entry.setPercentNetProfitability(entry.getNetProfitability() / currentOriginalValue);
    }

    public void delEntry(InvestmentAccountEntry entry, Integer groupId) {

        if (!entry.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){
            List<QuoteSale> sales = quoteSaleRepository.findAllBySaleId(entry.getId());
            for (QuoteSale quoteSale: sales) {
                InvestmentAccountEntry purchaseEntry = (InvestmentAccountEntry) quoteSale.getPurchaseEntry();
                purchaseEntry.setQuotesAvailable(purchaseEntry.getQuotesAvailable() + quoteSale.getQtdQuotes());
                purchaseEntry.setAmount(purchaseEntry.getQuotesAvailable() * purchaseEntry.getQuoteValue());
                save(purchaseEntry, groupId);

                quoteSaleRepository.delete(quoteSale);
            }
        }
        accountEntryRepository.delete(entry);
    }

    /**
     * Save an entry, checking if it belongs to current user.
     * @param entry
     * @return
     */
    public InvestmentAccountEntry saveEntry(InvestmentAccountEntry entry, Integer groupId){
        entry.setQuoteLastValue(entry.getQuoteValue());
        InvestmentAccount account = (InvestmentAccount) accountRepository.findOne(entry.getAccountId(), groupId);

        if (entry.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){
            entry = savePurchaseEntry(account, entry, groupId);
        } else if (entry.getOperation().equals(InvestmentAccountEntry.Operation.SALE)) {
            entry = saveSaleEntry(account, entry, groupId);
        } else if (entry.getOperation().equals(InvestmentAccountEntry.Operation.IR_LAW)){
            entry = saveIRLawEntry(account, entry, groupId);
        }

        updateLastPrice(new AssetPrice(account.getId(), entry.getDate(), entry.getQuoteLastValue()), groupId);

        return entry;
    }

    /**
     * Update all entries in this account with this new last quote value.
     * @param price
     * @param groupId
     */
    public void updateLastPrice(AssetPrice price, Integer groupId){
        InvestmentAccount account = (InvestmentAccount)accountRepository.findOne(price.getAccountId(), groupId);
        for (InvestmentAccountEntry entry: account.getEntries()) {
            entry.setQuoteLastValue(price.getValue());
        }
        accountRepository.save(account);
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
        Double qtdQuotesForCalc=0.0;
        Double incomeTaxPercent = getTaxRange(account, entry).taxRange;
        int i=0; Boolean done=false; Double quoteDiff=0.0;
        while(i<entries.size() && !done){
            InvestmentAccountEntry entryToSell = entries.get(i);

            if (entryToSell.getOperation().equals(InvestmentAccountEntry.Operation.PURCHASE)){

                quoteDiff = AppMaths.round(entryToSell.getQuotesAvailable() - qtdQuotesToSell,8);
                if (quoteDiff >= 0){
                    entryToSell.setQuotesAvailable(AppMaths.round(entryToSell.getQuotesAvailable() - qtdQuotesToSell,8));
                    entryToSell.setAmount(entryToSell.getQuotesAvailable() * entryToSell.getQuoteValue());

                    quoteSaleRepository.save(new QuoteSale(entryToSell, entry, qtdQuotesToSell, 0.0));

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
                    quoteSaleRepository.save(new QuoteSale(entryToSell, entry, entryToSell.getQuotesAvailable(), 0.0));

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

    private IncomeTaxRateService.TaxRange getTaxRange(InvestmentAccount account, InvestmentAccountEntry entry){
        return incomeTaxRateService.getTaxRange(account, entry);
    }

    private Double getMaxTaxRange(InvestmentAccount account){
        return incomeTaxRateService.getMaxTaxRange(account);

    }
}
