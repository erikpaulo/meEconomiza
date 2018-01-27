package com.softb.savefy.patrimony.service;


import com.softb.savefy.account.service.StockPriceTask;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(StartupApplicationListener.class);

    @Inject
    private BenchmarkTask benchmarkTask;

    @Inject
    private StockPriceTask stockPriceTask;

    @Override public void onApplicationEvent(ContextRefreshedEvent event) {
        benchmarkTask.updateBenchmarks();
        stockPriceTask.updateLastPrice();
    }
}