package com.softb.savefy.patrimony.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.Institution;
import com.softb.savefy.account.model.InvestmentAccount;
import com.softb.savefy.account.model.StockAccount;
import com.softb.savefy.account.service.AccountsService;
import com.softb.savefy.account.service.CheckingAccountService;
import com.softb.savefy.patrimony.model.Benchmark;
import com.softb.savefy.patrimony.model.Patrimony;
import com.softb.savefy.patrimony.model.PatrimonyEntry;
import com.softb.savefy.patrimony.repository.BenchmarkRepository;
import com.softb.savefy.patrimony.repository.PatrimonyEntryRepository;
import com.softb.savefy.patrimony.repository.PatrimonyRepository;
import com.softb.savefy.utils.AppDate;
import com.softb.savefy.utils.AppMaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

/**
 * Created by eriklacerda on 3/1/16.
 */
@Service
public class PatrimonyService {

    @Autowired
    private PatrimonyRepository patrimonyRepository;

    @Autowired
    private PatrimonyEntryRepository patrimonyEntryRepository;

    @Autowired
    private BenchmarkRepository benchmarkRepository;

    @Inject
    private AccountsService accountsService;

    @Inject
    private CheckingAccountService checkingAccountService;


    public Patrimony baseline (Patrimony patrimony, Integer groupId){
        Date month = AppDate.getMonthDate(patrimony.getDate());

        // Check if there's already a patrimony for this date.
        Patrimony pPatrimony = patrimonyRepository.findByDate(month, groupId);
        if (pPatrimony != null){
            patrimonyRepository.delete(pPatrimony);
        }
        patrimony = generate(month, groupId);
        patrimonyRepository.save(patrimony);

        return load(AppDate.getMonthDate(new Date()), groupId);
    }

    public Patrimony load (Date date, Integer groupId){

        Patrimony patrimony = generate(date, groupId);
        return patrimony;
    }

    public Patrimony generate(Date month, Integer groupId){
        Map<String, Double> riskMap = new HashMap<>();
        Map<String, Double> investTypeMap = new HashMap<>();
        Map<Date, Double> liquidityMap = new TreeMap<>();
        Map<Date, Benchmark> benchmarkMap;

        List<Account> accounts = accountsService.getAllActiveAccounts(groupId);

        benchmarkMap = getBenchmarkMap(month);

        List<Institution> institutions = accountsService.getInstitutions();
        Map<Integer, String> institutionMap = new HashMap<>();
        for (Institution institution: institutions) {
            institutionMap.put(institution.getId(), institution.getName());
        }

        Calendar prevMonth = Calendar.getInstance();
        prevMonth.setTime(month);
        prevMonth.add(Calendar.MONTH, -1);

        Patrimony patrimony = new Patrimony(month, groupId);
        Double totalBalance = 0.0, investedBalance = 0.0, totalProfit = 0.0, totalIncreasedProfit=0.0, totalIncreasedBalance=0.0;
        Double pctTotalIncreasedBalance=0.0, pctTotalIncreasedProfit=0.0;
        for (Account account: accounts) {
            Double increasedProfit = 0.0, pctIncreasedProfit = 0.0, increasedBalance=0.0, pctIncreasedBalance=0.0;

            Double profit = getProfit(account);
            Double balance = account.getBalance();

            List<PatrimonyEntry> hEntries =  patrimonyEntryRepository.findByAccount(account.getId(), groupId);
            // Sort
            Collections.sort(hEntries, new Comparator<PatrimonyEntry>(){
                public int compare(PatrimonyEntry o1, PatrimonyEntry o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            PatrimonyEntry pEntry = null;
            if (hEntries != null && hEntries.size()>0){
                pEntry = hEntries.get(hEntries.size()-1);
            }
            if (pEntry != null) {
                increasedBalance = account.getBalance() - pEntry.getBalance();
                pctIncreasedBalance = (pEntry.getBalance()>0?AppMaths.round((increasedBalance / pEntry.getBalance())*100, 2) : 0.0);

                increasedProfit = profit - pEntry.getProfit();
                pctIncreasedProfit = (pEntry.getProfit()>0?AppMaths.round((profit-pEntry.getProfit()) / pEntry.getProfit()*100, 2) : 0.0);
            }

            totalBalance += balance;
            totalProfit += profit;
            totalIncreasedProfit += increasedProfit;
            investedBalance += ( accountsService.isInvestType(account) ? balance : 0.0 );

            PatrimonyEntry entry = new PatrimonyEntry(account.getName(), month, account.getType(), account.getId(), balance,
                                                      increasedBalance, pctIncreasedBalance, profit, increasedProfit, pctIncreasedProfit, groupId,
                                                      institutionMap.get(account.getInstitutionId()), hEntries);
            patrimony.getEntries().add(entry);

            mapRisk(account, riskMap);
            mapInvestType(account, investTypeMap);
            mapLiquidity(account, liquidityMap);

        }

        List<Patrimony> history = patrimonyRepository.findAll(groupId);
        // Sort
        Collections.sort(history, new Comparator<Patrimony>(){
            public int compare(Patrimony o1, Patrimony o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        Patrimony pPatrimony = null;
        if (history != null && history.size()>0){
            pPatrimony = history.get(history.size()-1);
        }
        if (pPatrimony != null){
            totalIncreasedBalance = totalBalance - pPatrimony.getBalance();
            totalIncreasedProfit  = totalProfit - pPatrimony.getProfit();
            pctTotalIncreasedBalance = (pPatrimony.getBalance()>0 ? (totalIncreasedBalance / pPatrimony.getBalance())*100 : 0.0);
            pctTotalIncreasedProfit = (pPatrimony.getProfit() > 0 ? (totalIncreasedProfit / pPatrimony.getProfit())*100 : 0.0);
        }


        patrimony.setBalanceInvested(AppMaths.round(investedBalance,2));
        patrimony.setBalance(AppMaths.round(totalBalance,2));
        patrimony.setIncreasedBalance(AppMaths.round(totalIncreasedBalance,2));
        patrimony.setPctIncreasedBalance(AppMaths.round(pctTotalIncreasedBalance,2));

        patrimony.setProfit(totalProfit);
        patrimony.setIncreasedProfit(totalIncreasedProfit);
        patrimony.setPctIncreasedProfit(pctTotalIncreasedProfit);

        patrimony.setRiskMap(riskMap);
        patrimony.setInvestTypeMap(investTypeMap);
        patrimony.setLiquidityMap(liquidityMap);
        patrimony.setBenchmarkMap(benchmarkMap);

        patrimony.setHistory(history);

        return patrimony;
    }

    private Map<Date, Benchmark> getBenchmarkMap(Date month) {
        Map<Date, Benchmark> benchmarkMap = new TreeMap<>();

        List<Benchmark> benchmarks = benchmarkRepository.findAll();
        for (Benchmark benchmark: benchmarks) {
            benchmarkMap.put(benchmark.getDate(), benchmark);
        }

        return benchmarkMap;
    }

    private Double getProfit(Account account){
        Double profit = 0.0;

        if ( accountsService.isInvestType(account)  ){
            if (account.getType().equals(Account.Type.INV)){
                profit = ((InvestmentAccount) account).getNetProfit();
            } else if (account.getType().equals(Account.Type.STK)){
                profit = ((StockAccount)account).getNetProfit();
            }
        }

        return profit;
    }

    private void mapLiquidity(Account account, Map<Date, Double> liquidityMap) {
        if (accountsService.isInvestType(account)){
            Date date = account.getLiquidityDate();

            if (liquidityMap.get(date) == null) liquidityMap.put(date, 0.0);
            liquidityMap.put(date, liquidityMap.get(date) + account.getBalance());
        }
    }

    private void mapRisk(Account account, Map<String, Double> riskMap){
        if (accountsService.isInvestType(account)){
            if (account != null && account.getRisk() != null){
                if (riskMap.get(account.getRisk()) == null) riskMap.put(account.getRisk(), 0.0);
                riskMap.put(account.getRisk(), riskMap.get(account.getRisk()) + account.getBalance());
            }
        }
    }

    private void mapInvestType(Account account, Map<String, Double> investTypeMap){

        if (account != null){

            String code = "OTHERS";
            if (account instanceof StockAccount){
                code = account.getType().toString();
            } else if (account instanceof InvestmentAccount){
                code = ((InvestmentAccount) account).getProduct().toString();
            } else {
                return;
            }

            if (investTypeMap.get(code) == null) investTypeMap.put(code, 0.0);
            investTypeMap.put(code, investTypeMap.get(code) + account.getBalance());
        }
    }
}
