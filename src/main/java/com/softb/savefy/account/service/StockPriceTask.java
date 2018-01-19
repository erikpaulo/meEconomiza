package com.softb.savefy.account.service;

import com.softb.savefy.account.model.AccountEntry;
import com.softb.savefy.account.model.StockAccountEntry;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.system.errorhandler.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Atualiza os preços de todas as ações registradas com posições ativas
 */
@Component
public class StockPriceTask  {

    @Autowired
    private AccountEntryRepository stockRepository;

    @Inject
    private StockAccountService stockAccountService;

    private static final Logger log = LoggerFactory.getLogger(StockPriceTask.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(cron = "0 0/15 10-18 * * MON-FRI")
    public void reportCurrentTime() {
        List<AccountEntry> stocks = stockRepository.listAllActiveStocks();

        for (AccountEntry entry: stocks) {
            StockAccountEntry stock = (StockAccountEntry) entry;

            stock.setLastPrice( getStockPrice(stock) );
            stockAccountService.updateCurrentPosition(stock, stock.getGroupId());

            log.info("Price updated for {}", stock.getCode());
        }
    }

    private Double getStockPrice( StockAccountEntry stockEntry){
        Stock stock = null;
        try {
            stock = YahooFinance.get(stockEntry.getCode() + ".SA");
        } catch (IOException e){
            new SystemException(e.getMessage());
        }

        BigDecimal price = stock.getQuote().getPrice();
//        BigDecimal change = stock.getQuote().getChangeInPercent();
//        BigDecimal peg = stock.getStats().getPeg();
//        BigDecimal dividend = stock.getDividend().getAnnualYieldPercent();

        return price.doubleValue();
    }
}

