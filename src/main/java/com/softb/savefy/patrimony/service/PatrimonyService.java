package com.softb.savefy.patrimony.service;

import com.softb.savefy.account.model.Account;
import com.softb.savefy.account.model.Institution;
import com.softb.savefy.account.model.InvestmentAccount;
import com.softb.savefy.account.model.StockAccount;
import com.softb.savefy.account.service.AccountsService;
import com.softb.savefy.account.service.CheckingAccountService;
import com.softb.savefy.patrimony.model.Patrimony;
import com.softb.savefy.patrimony.model.PatrimonyEntry;
import com.softb.savefy.patrimony.repository.PatrimonyEntryRepository;
import com.softb.savefy.patrimony.repository.PatrimonyRepository;
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

    @Inject
    private AccountsService accountsService;

    @Inject
    private CheckingAccountService checkingAccountService;

    public Patrimony baseline (Patrimony patrimony, Integer groupId){

        // Check if there's already a patrimony for this date.
        Patrimony pPatrimony = patrimonyRepository.findByDate(patrimony.getDate(), groupId);
        if (pPatrimony != null){
            patrimonyRepository.delete(pPatrimony);
        }
        patrimonyRepository.save(patrimony);

        return load(patrimony.getDate(), groupId);
    }

    public Patrimony load (Date date, Integer groupId){

        Patrimony patrimony = generate(date, groupId);
        List<Patrimony> history = patrimonyRepository.findAll(groupId);

        // Sort
        Collections.sort(history, new Comparator<Patrimony>(){
            public int compare(Patrimony o1, Patrimony o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        for (PatrimonyEntry entry: patrimony.getEntries()) {
            List<PatrimonyEntry> hEntries =  patrimonyEntryRepository.findByAccount(entry.getAccountId(), groupId);

            // Sort
            Collections.sort(hEntries, new Comparator<PatrimonyEntry>(){
                public int compare(PatrimonyEntry o1, PatrimonyEntry o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });

            entry.setHistory(hEntries);
        }

        patrimony.setHistory(history);

        return patrimony;
    }

    public Patrimony generate(Date month, Integer groupId){
        Map<String, Double> riskMap = new HashMap<>();
        Map<String, Double> investTypeMap = new HashMap<>();
        Map<Date, Double> liquidityMap = new HashMap<>();

        List<Account> accounts = accountsService.getAllActiveAccounts(groupId);

        List<Institution> institutions = accountsService.getInstitutions();
        Map<Integer, String> instutionsMap = new HashMap<>();
        for (Institution institution: institutions) {
            instutionsMap.put(institution.getId(), institution.getName());
        }

        Calendar thisMonth = Calendar.getInstance();
        thisMonth.setTime(month);
        thisMonth.set(Calendar.DAY_OF_MONTH, 01);

        Calendar prevMonth = Calendar.getInstance();
        prevMonth.setTime(thisMonth.getTime());
        prevMonth.add(Calendar.MONTH, -1);

        Patrimony patrimony = new Patrimony(thisMonth.getTime(), groupId);
        Double totalBalance = 0.0, investedBalance = 0.0, totalProfit = 0.0, investedProfit = 0.0;
        for (Account account: accounts) {
            PatrimonyEntry pEntry = patrimonyEntryRepository.findByDateAccount(prevMonth.getTime(), account.getId(), groupId);

            Double profit = 0.0, percentProfit = 0.0;
            if (pEntry != null) {
                profit = account.getBalance() - pEntry.getBalance();
                investedProfit +=( accountsService.isInvestType(account) ? profit : 0.0 );

                percentProfit = AppMaths.round(profit / pEntry.getBalance(), 2);
                totalProfit += profit;
            }

            totalBalance += account.getBalance();
            investedBalance += ( accountsService.isInvestType(account) ? account.getBalance() : 0.0 );
            PatrimonyEntry entry = new PatrimonyEntry(account.getName(), thisMonth.getTime(), account.getType(), account.getId(),account.getBalance(), profit,
                                                      percentProfit, groupId, instutionsMap.get(account.getInstitutionId()), null);
            patrimony.getEntries().add(entry);

            mapRisk(account, riskMap);
            mapInvestType(account, investTypeMap);
            mapLiquidity(account, liquidityMap);

        }
        Patrimony pPatrimony = patrimonyRepository.findByDate(prevMonth.getTime(), groupId);

        patrimony.setBalanceTotal(totalBalance);
        patrimony.setBalanceInvested(investedBalance);
        patrimony.setProfitTotal(totalProfit);
        patrimony.setProfitInvested(investedProfit);
        patrimony.setPercentProfitTotal((pPatrimony != null ? patrimony.getProfitTotal() / pPatrimony.getBalanceTotal() : 0.0));
        patrimony.setPercentProfitInvested((pPatrimony != null ? patrimony.getProfitInvested() / pPatrimony.getBalanceInvested() : 0.0));
        patrimony.setRiskMap(riskMap);
        patrimony.setInvestTypeMap(investTypeMap);
        patrimony.setLiquidityMap(liquidityMap);

        return patrimony;
    }

    private void mapLiquidity(Account account, Map<Date, Double> liquidityMap) {
        if (accountsService.isInvestType(account)){
            Date date = account.getLiquidityDate();

            if (liquidityMap.get(date) == null) liquidityMap.put(date, 0.0);
            liquidityMap.put(date, liquidityMap.get(date) + account.getBalance());
        }
    }

    private void mapRisk(Account account, Map<String, Double> riskMap){
        if (account != null && account.getRisk() != null){
            if (riskMap.get(account.getRisk()) == null) riskMap.put(account.getRisk(), 0.0);
            riskMap.put(account.getRisk(), riskMap.get(account.getRisk()) + account.getBalance());
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
