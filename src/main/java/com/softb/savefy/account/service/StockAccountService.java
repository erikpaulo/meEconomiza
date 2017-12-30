package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.AssetPriceRepository;
import com.softb.savefy.account.repository.QuoteSaleRepository;
import com.softb.savefy.utils.AppMaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class, put together all services user can do with checking account (and similar) accounts.
 */
@Service
public class StockAccountService extends AbstractAccountService {

    public static final int DAYS_OF_YEAR = 360;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private QuoteSaleRepository quoteSaleRepository;

    @Autowired
    private AssetPriceRepository indexRepository;

    /**
     * Save a new Investment into the system
     * @param account
     * @param groupId
     * @return
     */
    public StockAccount saveAccount(StockAccount account, Integer groupId){
        account = accountRepository.save(account);
        return account;
    }

    /**
     * Return the account with all its dependencies;
     * @param id
     * @param groupId
     * @return
     */
    public StockAccount getAccountDetailed(Integer id, Integer groupId){
        StockAccount account = (StockAccount)get(id, groupId);
        calcAccountBalance(account);
        return account;
    }

    /**
     * Calulate the account balance, considering the current value of its stocks.
     * @param account
     */
    @Override
    public void calcAccountBalance(Account account) {
        StockAccount stockPortfolio = (StockAccount) account;

        Double balance = 0.0;
        for (StockAccountEntry entry: ((StockAccount) account).getStocks()) {
            if (entry.getOperation().equals(StockAccountEntry.Operation.PURCHASE)){
//                calcGains(entry);
                balance += entry.getCurrentValue();
            }
        }
        account.setBalance(balance);
    }

    /**
     * Calculate all calculated field of an investment entry, considering last price
     * @param stock
     */
    private void calcGains(StockAccountEntry stock){
        stock.setAmount(stock.getQuantity() * stock.getOriginalPrice());
        stock.setCurrentValue(stock.getQuantity() * stock.getLastPrice());
        stock.setGrossProfitability(stock.getCurrentValue() - stock.getAmount());
        stock.setNetProfitability(stock.getGrossProfitability() - stock.getBrokerage());
        stock.setPercentGrossProfitability((stock.getAmount() > 0 ?stock.getGrossProfitability() / stock.getAmount()*100 : 0.0));
        stock.setPercentNetProfitability((stock.getAmount() > 0 ? stock.getNetProfitability() / stock.getAmount()*100 : 0.0));

////        AssetPrice currentPrice = getLastPrice(stock, stock.getGroupId());
////        stock.setCurrentValue(stock.getQuantity() * currentPrice.getValue());
//        Double originalValue = (stock.getQuantity() * stock.getOriginalPrice());
//        stock.setGrossProfitability(stock.getCurrentValue() - originalValue);
//        stock.setNetProfitability(stock.getGrossProfitability() - stock.getBrokerage());
//        stock.setPercentGrossProfitability(stock.getGrossProfitability() / originalValue);
//        stock.setPercentNetProfitability(stock.getNetProfitability() / originalValue);
    }

    public void delEntry(StockAccountEntry entry, Integer groupId) {

        if (!entry.getOperation().equals(StockAccountEntry.Operation.PURCHASE)){
            List<QuoteSale> sales = quoteSaleRepository.findAllBySaleId(entry.getId());
            for (QuoteSale quoteSale: sales) {
                StockAccountEntry purchaseEntry = (StockAccountEntry) quoteSale.getPurchaseEntry();
                purchaseEntry.setQuantity(purchaseEntry.getQuantity() + quoteSale.getQtdQuotes());
//                purchaseEntry.setAmount(purchaseEntry.getQuantity() * purchaseEntry.getOriginalPrice());
                calcGains(purchaseEntry);
                save(purchaseEntry, groupId);

                quoteSaleRepository.delete(quoteSale);
            }
        }
        accountEntryRepository.delete(entry);
    }

    /**
     * Save an entry, checking if it belongs to current user.
     * @param stock
     * @return
     */
    public StockAccountEntry saveEntry(StockAccountEntry stock, Integer groupId){
        StockAccount stockPortfolio = (StockAccount) accountRepository.findOne(stock.getAccountId(), groupId);

        if (stock.getOperation().equals(StockAccountEntry.Operation.PURCHASE)){
            stock = buyStock(stockPortfolio, stock, groupId);
        } else if (stock.getOperation().equals(StockAccountEntry.Operation.SALE)) {
            stock = sellStock(stockPortfolio, stock, groupId);
        }

//        if (stock.getId() == null){
//            indexRepository.save(new AssetPrice(null, stock.getId(), stock.getDate(), stock.getQuantity(), groupId));
//        }

        return stock;
    }

//    private AssetPrice getLastPrice(StockAccountEntry stock, Integer groupId){
//        AssetPrice assetPrice = null;
//        List<AssetPrice> assetPrices = stock.getAssetPrices();
//
//
//        if (assetPrices==null || assetPrices.size() <= 0){
//            assetPrice = new AssetPrice(null, stock.getId(), new Date(), 0.0, groupId);
//        } else {
//            // Sort Conciliations DESC
//            Collections.sort(assetPrices, new Comparator<AssetPrice>(){
//                public int compare(AssetPrice o1, AssetPrice o2) {
//                    return o2.getDate().compareTo(o1.getDate());
//                }
//            });
//
//            assetPrice = assetPrices.get(0);
//        }
//
//        return assetPrice;
//    }

    public StockAccountEntry setLastPrice(StockAccountEntry stock, Integer groupId){
        calcGains(stock);
        return (StockAccountEntry) save(stock, groupId);
    }

    private StockAccountEntry buyStock(StockAccount stockPortfolio, StockAccountEntry stock, Integer groupId){
        stock.setLastPrice(stock.getOriginalPrice());
        calcGains(stock);

        return (StockAccountEntry) save(stock, groupId);
    }

    private StockAccountEntry sellStock(StockAccount stockPortfolio, StockAccountEntry stock, Integer groupId){
        List<StockAccountEntry> stocks = stockPortfolio.getStocks();

        // Sort
        Collections.sort(stocks, new Comparator<StockAccountEntry>(){
            public int compare(StockAccountEntry o1, StockAccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        stock = (StockAccountEntry) save(stock, groupId);

        Double quantityToSell = stock.getQuantity();

        Double originalAmount=0.0, grossProfit=0.0, percentGrossProfit=0.0, netProfit=0.0, percentNetProfit=0.0;
        Double qtdQuotesForCalc=0.0;
        int i=0; Boolean done=false; Double quoteDiff=0.0;
        while(i<stocks.size() && !done){
            StockAccountEntry stockToSell = stocks.get(i);

            if (stockToSell.getOperation().equals(StockAccountEntry.Operation.PURCHASE)
                    && stock.getCode().equals(stockToSell.getCode())
                    && stockToSell.getQuantity() > 0){
                stock.setOriginalPrice(stockToSell.getOriginalPrice());

                quoteDiff = AppMaths.round(stockToSell.getQuantity() - quantityToSell,8);
                if (quoteDiff >= 0){
                    stockToSell.setQuantity(stockToSell.getQuantity() - quantityToSell);
//                    stockToSell.setAmount(stockToSell.getQuantity() * stockToSell.getOriginalPrice());

                    quoteSaleRepository.save(new QuoteSale(stockToSell, stock, quantityToSell));
                    calcGains(stockToSell);
                    save(stockToSell, groupId);

                    qtdQuotesForCalc = quantityToSell;
                    done = true;
                } else {
                    qtdQuotesForCalc = stockToSell.getQuantity();
                    quoteSaleRepository.save(new QuoteSale(stockToSell, stock, stockToSell.getQuantity()));

                    stockToSell.setQuantity(0.0);
//                    stockToSell.setAmount(0.0);
                    calcGains(stockToSell);
                    save(stockToSell, groupId);

                    quantityToSell -= stockToSell.getQuantity();
                }

                // Calc gains
                originalAmount += (stock.getOriginalPrice() * qtdQuotesForCalc);
                grossProfit += (qtdQuotesForCalc * stock.getLastPrice()) - (stock.getOriginalPrice() * qtdQuotesForCalc);
                netProfit += grossProfit - stock.getBrokerage();
            }

            i++;
        }

        percentGrossProfit = grossProfit / originalAmount * 100;
        percentNetProfit = netProfit / originalAmount * 100;

        stock.setGrossProfitability(grossProfit);
        stock.setNetProfitability(netProfit);
        stock.setPercentGrossProfitability(percentGrossProfit);
        stock.setPercentNetProfitability(percentNetProfit);
        stock.setCurrentValue(stock.getAmount());
        stock.setAmount(originalAmount);

        return (StockAccountEntry) save(stock, groupId);
    }
}
