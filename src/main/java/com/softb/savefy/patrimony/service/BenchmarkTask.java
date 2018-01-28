package com.softb.savefy.patrimony.service;

import com.softb.savefy.account.service.StockPriceTask;
import com.softb.savefy.patrimony.model.Benchmark;
import com.softb.savefy.patrimony.repository.BenchmarkRepository;
import com.softb.savefy.utils.AppDate;
import com.softb.savefy.utils.AppMaths;
import com.softb.savefy.utils.Constants;
import com.softb.system.errorhandler.exception.SystemException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Atualiza os preços de todas as ações registradas com posições ativas
 */
@Component
public class BenchmarkTask {
    protected static final String CETIP_URL = "https://www.cetip.com.br/";
    protected static final String CETIP_CDI_SELECTOR = "ctl00_Banner_lblTaxDI";

    protected static final String IBOV_CODE = "^BVSP";

    private static final Logger log = LoggerFactory.getLogger(StockPriceTask.class);

    @Autowired
    private BenchmarkRepository benchmarkRepository;

    @Scheduled(cron = "0 0 20 * * *", zone = Constants.TIMEZONE_PTBR)
    public void updateBenchmarks(){
        Benchmark benchmark = benchmarkRepository.findOneByDate(AppDate.getMonthDate(AppDate.today()));
        if (benchmark == null){
            benchmark = new Benchmark(AppDate.getMonthDate(new Date()), null, null);
        }

        Double ibov = getIBOV();
        if (ibov != null){
            benchmark.setIBovespa(ibov);
        }
        log.info("IBOV: {}", ibov);


        Double cdi = getCdI();
        if (cdi != null){
            benchmark.setCdi(cdi);
        }
        log.info("CDI: {}", cdi);

        benchmarkRepository.save(benchmark);
    }

    /**
     * Índice da bolsa de SP
     * @return
     */
    private Double getIBOV(){
        log.info("Getting current iBov index...");
        Stock stock;

        Calendar monthBegin = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        monthBegin.set(Calendar.DAY_OF_MONTH, 01);
        Calendar monthEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        try {
            stock = YahooFinance.get(IBOV_CODE, monthBegin, monthEnd, Interval.MONTHLY);
        } catch (IOException e){
            log.error("Couldn't get IBOV", e);
            return null;
        }

        BigDecimal startValue, endValue;
        try {
            startValue = stock.getHistory().get(0).getOpen();
            endValue = stock.getHistory().get(1).getClose();
        } catch (IOException e) {
            log.error("Couldn't get IBOV", e);
            return null;
        }

        return AppMaths.round( ((endValue.doubleValue() - startValue.doubleValue()) / startValue.doubleValue())*100,2);
    }

    /**
     * Índice CDI
     * @return
     */
    private  Double getCdI(){

        Element element = getElementById(CETIP_URL, CETIP_CDI_SELECTOR);
        if (element == null){
            return null;
        }

        Node nCDI = element.childNode(0);
        String sCDI = nCDI.toString();

        return Double.parseDouble(sCDI.replace("%", "").replace(",", "."));
    }

    private Element getElementById(String url, String id){
        Document doc = null;

        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            log.error("Connection error, trying again in 1 minute", e);
            try {
                TimeUnit.MINUTES.sleep(1);
                doc = Jsoup.connect(url).get();
            } catch (IOException e1) {
                log.error("Couldn't get CDI", e1);
                return null;
            } catch (InterruptedException e2){
                throw new SystemException(e.getMessage());
            }
        }

        return doc.getElementById(id);
    }
}

