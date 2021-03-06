package com.softb.savefy.account.service;

import com.softb.savefy.account.model.AccountEntry;
import com.softb.savefy.account.model.StockAccountEntry;
import com.softb.savefy.account.repository.AccountEntryRepository;
import com.softb.savefy.utils.Constants;
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


    @Scheduled(cron = "0 */15 9-19 * * MON-FRI", zone = Constants.TIMEZONE_PTBR)
    public void updateLastPrice() {
        List<AccountEntry> stocks = stockRepository.listAllActiveStocks();

        for (AccountEntry entry: stocks) {
            StockAccountEntry stock = (StockAccountEntry) entry;

            stock.setLastPrice( getStockPrice(stock.getCode()) );
            stockAccountService.updateCurrentPosition(stock, stock.getGroupId());

            log.info("Price updated for {}", stock.getCode());
        }
    }

    protected Double getStockPrice( String code){
        Stock stock = null;
        try {
            stock = YahooFinance.get(code + ".SA");
        } catch (IOException e){
            new SystemException(e.getMessage());
        }

        BigDecimal price = stock.getQuote().getPrice();

        return price.doubleValue();
    }
}

