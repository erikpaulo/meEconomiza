package com.softb.savefy.account.service;

import com.softb.savefy.account.model.*;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.account.repository.AccountRepository;
import com.softb.savefy.account.repository.QuoteSaleRepository;
import com.softb.savefy.account.repository.StockSaleProfitRepository;
import com.softb.savefy.account.web.resource.Stock;
import com.softb.savefy.account.web.resource.StockOperation;
import com.softb.savefy.utils.AppDate;
import com.softb.savefy.utils.AppMaths;
import com.softb.system.errorhandler.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * This class, put together all services user can do with stock portfolios.
 */
@Service
public class StockAccountService extends AbstractAccountService {

    public static final int DAYS_OF_YEAR = 360;
    public static final int RANGE_TAX_FREE = 20000;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountEntryRepository accountEntryRepository;

    @Autowired
    private QuoteSaleRepository quoteSaleRepository;

    @Autowired
    private StockSaleProfitRepository stockSaleProfitRepository;

    @Inject
    private CheckingAccountService checkingAccountService;

    @Inject
    private StockPriceTask stockPriceTask;

    @Inject
    private IncomeTaxRateService incomeTaxRateService;

    /**
     * Save a new Investment into the system
     * @param account
     * @param groupId
     * @return
     */
    public StockAccount saveAccount(StockAccount account, Integer groupId){
        account = accountRepository.save(account);
        calcAccountBalance(account);
        return account;
    }

    /**
     * Registra o pagamento de um IR
     * @param sale
     * @param groupId
     * @return
     */
    public StockSaleProfit registerStockProfitTaxPayment(StockSaleProfit sale, Integer groupId){
        sale.setType(StockSaleProfit.Type.IR);
        sale.setNegotiated(0.0);
        sale.setProfit(0.0);
        sale.setDate(AppDate.getMonthDate(sale.getDate()));
        sale.setGroupId(groupId);
        sale.setIncomeTax(-sale.getIncomeTax());
        return stockSaleProfitRepository.save(sale);
    }

    /**
     *
     * @param operation
     */
    public void registerStockOperation(StockOperation operation, Integer groupId){
        Double brokerageTax = 0.00032157;
        Double brokerTax = 0.87;

        StockAccount portifolio = (StockAccount) accountRepository.findOne(operation.getAccountId(), groupId);

        for (Stock asset: operation.getAssets()) {
            Double brokerage = (asset.getQuantity() * asset.getOriginalPrice() * brokerageTax) + (brokerTax);
            StockAccountEntry stock = new StockAccountEntry(operation.getDate(), asset.getCode(), asset.getOperation(), Double.parseDouble(asset.getQuantity().toString()),
                                                           asset.getOriginalPrice(), brokerage, operation.getAccountId(), groupId);

            if (asset.getOperation().equals(StockAccountEntry.Operation.PURCHASE)){
                buyStock(portifolio, stock, groupId);
            } else {
                stock.setLastPrice(stock.getOriginalPrice());
                sellStock(portifolio, stock, groupId);
            }
        }
    }

    /**
     * Registra o pagamento de um IR
     * @param saleProfitId
     * @param groupId
     * @return
     */
    public void removeTaxPayment(Integer saleProfitId, Integer groupId){
        StockSaleProfit saleProfit = stockSaleProfitRepository.findOne(saleProfitId, groupId);
        if (saleProfit == null) throw new BusinessException("This entry doesn't belong to current user");
        stockSaleProfitRepository.delete(saleProfitId);
    }

    /**
     * Return the account with all its dependencies;
     * @param id
     * @param groupId
     * @return
     */
    public StockAccount getAccountDetailed(Integer id, Integer groupId){
        StockAccount account = (StockAccount)get(id, groupId);
        loadMontlyProfit(groupId, account);

        calcAccountBalance(account);
        return account;
    }

    private void loadMontlyProfit(Integer groupId, StockAccount stockPortfolio) {
        stockPortfolio.setMonthlyProfit(stockSaleProfitRepository.findAllByUser(groupId));

        // Sort
        Collections.sort(stockPortfolio.getMonthlyProfit(), new Comparator<StockSaleProfit>(){
            public int compare(StockSaleProfit o1, StockSaleProfit o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        Double profitBalance = 0.0, itBalance = 0.0;
        for (StockSaleProfit monthProfit: stockPortfolio.getMonthlyProfit()) {
            profitBalance += monthProfit.getProfit();
            itBalance += monthProfit.getIncomeTax();

            monthProfit.setProfitBalance(profitBalance);
            monthProfit.setItBalance(itBalance);
        }
    }

    /**
     * Calulate the account balance, considering the current profit of its stocks.
     * @param account
     */
    @Override
    public void calcAccountBalance(Account account) {
        Double grossBalance = 0.0, netBalance = 0.0, grossProfit = 0.0, netProfit = 0.0, originalValue = 0.0;
        StockAccount stockPortfolio = (StockAccount) account;

//        if (stockPortfolio.getMonthlyProfit() == null){
//            loadMontlyProfit(stockPortfolio.getGroupId(), stockPortfolio);
//        }

        if (stockPortfolio.getStocks() != null){
            for (StockAccountEntry entry: ((StockAccount) account).getStocks()) {
                if (entry.getOperation().equals(StockAccountEntry.Operation.PURCHASE) && entry.getQuantity() > 0){
                    grossBalance += entry.getCurrentValue();
                    netBalance += entry.getCurrentValue() - entry.getBrokerage();

                }
                grossProfit += entry.getGrossProfitability();
                netProfit += entry.getNetProfitability();
                originalValue += entry.getAmount();
            }
        }

        stockPortfolio.setBalance(AppMaths.round(netBalance,2));
        stockPortfolio.setGrossBalance(AppMaths.round(grossBalance,2));

        stockPortfolio.setGrossProfit(AppMaths.round(grossProfit,2));
        stockPortfolio.setNetProfit(AppMaths.round(netProfit,2));

        stockPortfolio.setPercentGrossProfit(AppMaths.round((originalValue>0 ? grossProfit / originalValue * 100 : 0.0),2));
        stockPortfolio.setPercentNetProfit(AppMaths.round((originalValue>0 ? netProfit / originalValue * 100 : 0.0),2));

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
    }

    /**
     * Remove uma entrada em um investimento ou fundo de ações, voltando ações de venda de quotas ou papeis
     * @param entry
     * @param groupId
     */
    public void delEntry(StockAccountEntry entry, Integer groupId) {

        if (!entry.getOperation().equals(StockAccountEntry.Operation.PURCHASE)){
            List<QuoteSale> sales = quoteSaleRepository.findAllBySaleId(entry.getId());
            for (QuoteSale quoteSale: sales) {
                StockAccountEntry purchaseEntry = (StockAccountEntry) quoteSale.getPurchaseEntry();
                purchaseEntry.setQuantity(purchaseEntry.getQuantity() + quoteSale.getQtdQuotes());
                purchaseEntry.setBrokerage(purchaseEntry.getBrokerage() + quoteSale.getBrokerage());
                calcGains(purchaseEntry);
                save(purchaseEntry, groupId);

                quoteSaleRepository.delete(quoteSale);
            }

            unregisterPortfolioProfit(entry, groupId);
        }
        accountEntryRepository.delete(entry);
    }

    /**
     * Atualiza a posição corrente desse papel. Geralmente invocado após atualização do ultimo preço do papel
     * @param entry
     * @param groupId
     * @return
     */
    public StockAccountEntry updateCurrentPosition(StockAccountEntry entry, Integer groupId){
        calcGains(entry);

        return (StockAccountEntry) save(entry, groupId);
    }


    /**
     * Realiza a compra de um papel
     * @param stockPortfolio
     * @param stock
     * @param groupId
     * @return
     */
    private StockAccountEntry buyStock(StockAccount stockPortfolio, StockAccountEntry stock, Integer groupId){

        Double lastPrice = stockPriceTask.getStockPrice(stock.getCode());
        if (lastPrice != null) {
            stock.setLastPrice(lastPrice);
        } else {
            stock.setLastPrice(stock.getOriginalPrice());
        }
        calcGains(stock);

        return (StockAccountEntry) save(stock, groupId);
    }

    @Transactional
    private StockAccountEntry sellStock(StockAccount stockPortfolio, StockAccountEntry stock, Integer groupId){
        List<StockAccountEntry> stocks = stockPortfolio.getStocks();

        // Sort
        Collections.sort(stocks, new Comparator<StockAccountEntry>(){
            public int compare(StockAccountEntry o1, StockAccountEntry o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        stock.setLastPrice(stock.getOriginalPrice());
        stock.setAmount(stock.getQuantity() * stock.getLastPrice());
        stock = (StockAccountEntry) save(stock, groupId);

        Double quantityToSell = stock.getQuantity();

        Double originalAmount=0.0, grossProfit=0.0, percentGrossProfit=0.0, netProfit=0.0, percentNetProfit=0.0;
        Double qtdQuotesForCalc=0.0, totalBrokerageToCarry=0.0, brokerageToCarry=0.0, percentToCarry=0.0;
        int i=0; Boolean done=false; Double quoteDiff=0.0;
        while(i<stocks.size() && !done){
            StockAccountEntry stockToSell = stocks.get(i);

            if (stockToSell.getOperation().equals(StockAccountEntry.Operation.PURCHASE)
                    && stock.getCode().equals(stockToSell.getCode())
                    && stockToSell.getQuantity() > 0){
                stock.setOriginalPrice(stockToSell.getOriginalPrice());

                quoteDiff = AppMaths.round(stockToSell.getQuantity() - quantityToSell,8);
                if (quoteDiff >= 0){

                    brokerageToCarry = stockToSell.getBrokerage() * (quantityToSell / stockToSell.getQuantity());

                    stockToSell.setBrokerage(stockToSell.getBrokerage() - brokerageToCarry);
                    stockToSell.setQuantity(stockToSell.getQuantity() - quantityToSell);

                    quoteSaleRepository.save(new QuoteSale(stockToSell, stock, quantityToSell, brokerageToCarry));

                    qtdQuotesForCalc = quantityToSell;
                    done = true;
                } else {
                    qtdQuotesForCalc = stockToSell.getQuantity();

                    quoteSaleRepository.save(new QuoteSale(stockToSell, stock, stockToSell.getQuantity(), stockToSell.getBrokerage()));

                    stockToSell.setBrokerage(stockToSell.getBrokerage() - brokerageToCarry);
                    stockToSell.setQuantity(0.0);

                    quantityToSell -= stockToSell.getQuantity();
                }
                calcGains(stockToSell);
                save(stockToSell, groupId);

                // Calc gains
                originalAmount += (stock.getOriginalPrice() * qtdQuotesForCalc);
                grossProfit += (qtdQuotesForCalc * stock.getLastPrice()) - (stock.getOriginalPrice() * qtdQuotesForCalc);
                totalBrokerageToCarry += brokerageToCarry;
            }

            i++;
        }

        percentGrossProfit = grossProfit / originalAmount * 100;

        stock.setGrossProfitability(grossProfit);
        stock.setBrokerage(stock.getBrokerage() + totalBrokerageToCarry);
        stock.setNetProfitability(grossProfit - stock.getBrokerage());
        stock.setPercentGrossProfitability(percentGrossProfit);
        stock.setPercentNetProfitability(stock.getNetProfitability() / originalAmount * 100);
        stock.setCurrentValue(stock.getQuantity() * stock.getLastPrice());
        stock.setAmount(originalAmount);

        registerPortfolioProfit(stock, groupId);

        return (StockAccountEntry) save(stock, groupId);
    }

    private void registerPortfolioProfit(StockAccountEntry stock, Integer groupId){
        Date date = AppDate.getMonthDate(stock.getDate());
        Double incomeTaxRange=0.0;

        StockSaleProfit monthlyProfit = stockSaleProfitRepository.findProfitByDateUser(date, groupId);
        if (monthlyProfit == null){
            monthlyProfit = new StockSaleProfit(date, StockSaleProfit.Type.PROFIT, 0.0, 0.0, 0.0, groupId,
                                                  0.0, 0.0);
        }
        monthlyProfit.setNegotiated(monthlyProfit.getNegotiated() + stock.getCurrentValue());
        monthlyProfit.setProfit(monthlyProfit.getProfit() + stock.getNetProfitability());

        if (!(monthlyProfit.getProfit() > 0 && monthlyProfit.getNegotiated() <= RANGE_TAX_FREE)){
            incomeTaxRange = incomeTaxRateService.getTaxRange(stock, false).getTaxRange();
        }
        monthlyProfit.setIncomeTax(monthlyProfit.getProfit() * incomeTaxRange);

        stockSaleProfitRepository.save(monthlyProfit);
    }

    private void unregisterPortfolioProfit(StockAccountEntry stock, Integer groupId){
        Date date = AppDate.getMonthDate(stock.getDate());
        Double incomeTaxRange=0.0;

        StockSaleProfit monthlyProfit = stockSaleProfitRepository.findProfitByDateUser(date, groupId);
        monthlyProfit.setNegotiated(monthlyProfit.getNegotiated() - stock.getCurrentValue());
        monthlyProfit.setProfit(monthlyProfit.getProfit() - stock.getNetProfitability());

        if (!(monthlyProfit.getProfit() > 0 && monthlyProfit.getNegotiated() <= RANGE_TAX_FREE)){
            incomeTaxRange = incomeTaxRateService.getTaxRange(stock, false).getTaxRange();
        }
        monthlyProfit.setIncomeTax(monthlyProfit.getProfit() * incomeTaxRange);

        stockSaleProfitRepository.save(monthlyProfit);
    }

}
