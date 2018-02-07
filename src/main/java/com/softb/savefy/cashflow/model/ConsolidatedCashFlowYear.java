package com.softb.savefy.cashflow.model;

import com.softb.savefy.utils.AppArray;
import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa o fluxo de entradas e saídas consolidado por subcategoria
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConsolidatedCashFlowYear extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Integer year;

	private List<Double> perMonthIncome;
	private List<Double> perMonthExpense;
	private List<Double> perMonthIncAverage;
	private List<Double> perMonthIncVariation;
	private List<Double> perMonthExpAverage;
	private List<Double> perMonthExpVariation;
	private List<Double> perMonthExpSuperfluous;
	private List<Double> perMonthExpEssential;

	protected List<ConsolidatedCashFlowYear> incomes;
	protected List<ConsolidatedCashFlowYear> expenses;
	protected List<ConsolidatedCashFlowYear> investments;

	public ConsolidatedCashFlowYear(Integer year){
		this.year = year;
		this.perMonthIncome = AppArray.initMonthValues();
		this.perMonthExpense = AppArray.initMonthValues();
		this.perMonthIncAverage = AppArray.initMonthValues();
		this.perMonthIncVariation = AppArray.initMonthValues();
		this.perMonthExpAverage = AppArray.initMonthValues();
		this.perMonthExpVariation = AppArray.initMonthValues();
		this.perMonthExpSuperfluous = AppArray.initMonthValues();
		this.perMonthExpEssential = AppArray.initMonthValues();

		this.incomes = new ArrayList<>();
		this.expenses = new ArrayList<>();
		this.investments = new ArrayList<>();
	}
}
