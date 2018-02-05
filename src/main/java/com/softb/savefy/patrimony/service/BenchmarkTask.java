package com.softb.savefy.patrimony.service;

import com.softb.savefy.account.service.StockPriceTask;
import com.softb.savefy.patrimony.model.Benchmark;
import com.softb.savefy.patrimony.repository.BenchmarkRepository;
import com.softb.savefy.utils.AppDate;
import com.softb.savefy.utils.AppMaths;
import com.softb.savefy.utils.Constants;
import com.softb.system.errorhandler.exception.SystemException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


/**
 * Atualiza os preços de todas as ações registradas com posições ativas
 */
@Component
public class BenchmarkTask {
    protected static final String CETIP_DI_QUERY = "http://www.cetip.com.br/astec/series_v05/paginas/simulador/simulador_v04_grupo_01_b.asp";
    protected static final String CETIP_CDI_LOGIN = "https://www.cetip.com.br/Paginas/LogarDI.aspx";

    protected static final String IBOV_CODE = "^BVSP";

    private static final Logger log = LoggerFactory.getLogger(StockPriceTask.class);

    @Autowired
    private BenchmarkRepository benchmarkRepository;

    @PostConstruct
    public void updateBenchmarksOnStartUp(){
        updateBenchmarks();
    }

    @Scheduled(cron = "0 */60 8-18 * * MON-FRI", zone = Constants.TIMEZONE_PTBR)
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

        Double startValue, endValue;
        try {
            startValue = stock.getHistory().get(0).getOpen().doubleValue();
            if (stock.getHistory().size()>1){
                endValue = stock.getHistory().get(1).getClose().doubleValue();
            } else {
                endValue = stock.getHistory().get(0).getClose().doubleValue();
            }
        } catch (IOException e) {
            log.error("Couldn't get IBOV", e);
            return null;
        }

        return AppMaths.round( ((endValue - startValue) / startValue)*100,2);
    }

    /**
     * Índice CDI
     * @return
     */
    private  Double getCdI(){

        Calendar monthBegin = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        monthBegin.set(Calendar.DAY_OF_MONTH, 01);
        Calendar monthEnd = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        Element element = getElementById(monthBegin, monthEnd);
        if (element == null){
            return null;
        }

        String sCDI = element.text();

        return Double.parseDouble(sCDI.replace("%", "").replace(",", ".").replace(" ", ""));
    }

    private Element getElementById(Calendar begin, Calendar end){
        Document doc = null;
        Connection query = null;

        try {

            Connection.Response loginDI = Jsoup.connect(CETIP_CDI_LOGIN)
                    .data("Navegador", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .referrer("https://www.cetip.com.br/Paginas/AcumuleDI.aspx")
                    .userAgent("Mozilla/5.0")
                    .method(Connection.Method.GET)
                    .execute();

            query =  Jsoup.connect(CETIP_DI_QUERY)
                    .cookies(loginDI.cookies())
                    .data("Navegador", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .data("__VIEWSTATE", "rmYIMO8+B22Enuq48gJ2Sy6QtsYr6h/ip558oCZiXFbd7WwojjuFawTE5BcbYCBa9EQUkNSYxtFslJB4EbcJP7WTy9B8ylR21ltg2VyEHKmj5FaWNuc+3rRz5HQJSaCRqcYtJJy4xWYo0+w6FerOzkaKoxPtbQjhJluSFM4tYp76wJtbVWY94FPzw8HAyL5GNRtameDPn71M93T6UCe1iOPmT3Um8lTE01ZxGmXnFIkzduDo")
                    .data("__VIEWSTATEGENERATOR", "4D16475F")
                    .data("__EVENTVALIDATION", "hO+qfNHt9OMOo0t2nCykn+/gZ+/Qxt9o3P13HD84PETv0Ua753mI9ZHY25ZfxIzS9anfD19+25eG5OHWw1rg+Sy9L1k8OoNZtFq3GCKD7Y4vbWLecfNoHGCMWdMFuOW/YfcEZand+8pM3cJ+ZKsGp+/WvVZMd4ezn+2zr6VuHpWSbYydxsiLnShzOkVLqjq/gCB04Zjuk8Sk/Nv4xsmaA4NVQYX6JsttzITCj0mdcNS5Fo3a3/dIKrfTfV6EGRvyngmVl7eX7aOaW7YziC6tfb7dXx8jvnfsgmbXrw4TFzJ/4gSJh4d5+45ACHxSzRhO/CXkechU/e5tTIBNOiDKFREF8Vm+yX8JLDx7Y/peaB1YmVd5V2jOJfXqlVR/Zc3wKaa7Bg==")
                    .data("DT_DIA_DE", Integer.toString(begin.get(Calendar.DAY_OF_MONTH)))
                    .data("DT_MES_DE", Integer.toString(begin.get(Calendar.MONTH)+1))
                    .data("DT_ANO_DE", Integer.toString(begin.get(Calendar.YEAR)))
                    .data("DT_DIA_ATE", Integer.toString(end.get(Calendar.DAY_OF_MONTH)))
                    .data("DT_MES_ATE", Integer.toString(end.get(Calendar.MONTH)+1))
                    .data("DT_ANO_ATE", Integer.toString(end.get(Calendar.YEAR)))
                    .data("var_perc", "100,00")
                    .data("var_valor", "100,00")
                    .data("var_indice", "3")
                    .data("var_idioma", "1")
                    .userAgent("Mozilla/5.0")
                    .referrer("https://www.cetip.com.br/Paginas/AcumuleDI.aspx")
                    .method(Connection.Method.GET);

            doc = query.post();
        } catch (IOException e) {
            log.error("Connection error, trying again in 1 minute", e);
            try {
                TimeUnit.MINUTES.sleep(1);
                doc = query.post();
            } catch (IOException e1) {
                log.error("Couldn't get CDI", e1);
                return null;
            } catch (InterruptedException e2){
                throw new SystemException(e.getMessage());
            }
        }

        return doc.select(":containsOwn(Taxa:) + td").get(0);
    }
}

