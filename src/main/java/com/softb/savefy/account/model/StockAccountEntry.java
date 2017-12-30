package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Classe que representa um papel de ação
 * @author Erik Lacerda
 *
 */
@Data
@Entity
@DiscriminatorValue("STK")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockAccountEntry extends AccountEntry implements Serializable {

	private static final long serialVersionUID = 1L;


	// DEFINE THE DATE, INDEPENDENT OF THE LIQUIDITY TYPE
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ENTRY_ID", referencedColumnName = "ID")
	protected List<AssetPrice> assetPrices;

	// PURCHASE OR SALE
	@Column(name = "STOCK_CODE")
	@NotEmpty
	protected String code;

	// PURCHASE OR SALE
	@Column(name = "OPERATION")
	@NotNull
	protected Operation operation;

	// QTD OF PAPERS
	@Column(name = "QUOTES")
	@NotNull
	protected Double quantity;

	// PRICE AT TIME OF INCLUSION
	@Column(name = "QUOTE_VALUE")
	@NotNull
	protected Double originalPrice;

	// LAST STOCK PRICE
    @Column(name = "LAST_PRICE")
    @NotNull
	protected Double lastPrice;

	// ROM THE TOTAL INVESTED, HOW MUCH IS AVAILABLE WITH PROFITABILITY
	@Column(name = "CURRENT_AMOUNT")
	protected Double currentValue;

	// TAX
	@Column(name = "IOF")
	protected Double brokerage;

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
		PURCHASE ( "Compra" ), SALE ( "Venda" );
		private String name;

		Operation(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
