package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as entradas de um investimento.
 * @author Erik Lacerda
 *
 */
@Data
@Entity
@DiscriminatorValue("INV")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvestmentAccountEntry extends AccountEntry implements Serializable {

	private static final long serialVersionUID = 1L;

	// PURCHASE OR SALE
	@Column(name = "OPERATION")
	@NotNull
	protected Operation operation;

	// QTD QUOTES PURCHASED
	@Column(name = "QUOTES")
	@NotNull
	protected Double quotes;

	// QUOTE VALUE AT TIME OF THE PURCHASE
	@Column(name = "QUOTE_VALUE")
	@NotNull
	protected Double quoteValue;

	// QTD QUOTES AVAILABLE NOW
	@Column(name = "QUOTES_AVAILABLE")
	@NotNull
	protected Double quotesAvailable;

	// ROM THE TOTAL INVESTED, HOW MUCH IS AVAILABLE WITH PROFITABILITY
	@Column(name = "CURRENT_AMOUNT")
	protected Double currentAmount;

	// TAX INCOME PERCENT (CONSIDERING TAX RANGE)
	@Column(name = "INCOME_TAX_PERCENT")
	protected Double incomeTaxPercent;

	// TAX INCOME AMOUNT (CONSIDERING TAX RANGE)
	@Column(name = "INCOME_TAX_AMOUNT")
	protected Double incomeTaxAmount;

	// DATE THIS ENTRY GET LOWER TAX RATE;
	@Transient
	protected Date nextTaxRangeDate;

	// GROSS PROFITABILITY AMOUNT
	@Column(name = "GROSS_PROFITABILITY")
	protected Double grossProfitability;

	// PERCENT GROSS PROFITABILITY
	@Column(name = "PERCENT_GROSS_PROFITABILITY")
	protected Double percentGrossProfitability;

	// NET PROFITABILITY AMOUNT
	@Column(name = "NET_PROFITABILITY")
	protected Double netProfitability;

	// PERCENT NET PROFITABILITY
	@Column(name = "PERCENT_NET_PROFITABILITY")
	protected Double percentNetProfitability;

	public enum Operation {
		PURCHASE ( "Aplicação" ), SALE ( "Resgate" ), IR_LAW ( "Come Cotas" );
		private String name;

		Operation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
