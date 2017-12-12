package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

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
	@NotEmpty
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

	// FROM THE TOTAL INVESTED, HOW MUCH IS AVAILABLE WITHOUT PROFITABILITY
	@Transient
	protected Double amountAvailable;

	// ROM THE TOTAL INVESTED, HOW MUCH IS AVAILABLE WITH PROFITABILITY
	@Transient
	protected Double currentAmount;

	//QUOTE VALUE AT TIME OF THE SALE
	@Column(name = "QUOTE_VALUE_SOLD")
	@NotNull
	protected Double quoteValueSold;

	// TAX INCOME PERCENT (CONSIDERING TAX RANGE)
	@Transient
	protected Double incomeTaxPercent;

	// TAX INCOME AMOUNT (CONSIDERING TAX RANGE)
	@Transient
	protected Double incomeTaxAmount;

	// GROSS PROFITABILITY AMOUNT
	@Transient
	protected Double grossProfitability;

	// PERCENT GROSS PROFITABILITY
	@Transient
	protected Double percentGrossProfitability;

	// NET PROFITABILITY AMOUNT
	@Transient
	protected Double netProfitability;

	// PERCENT NET PROFITABILITY
	@Transient
	protected Double percentNetProfitability;

	public enum Operation {
		PURCHASE ( "Aplicação" ), SALE ( "Resgate" );
		private String name;

		Operation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
