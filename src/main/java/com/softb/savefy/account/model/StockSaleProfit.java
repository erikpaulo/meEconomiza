package com.softb.savefy.account.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "STOCK_SALE")
public class StockSaleProfit extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "DATE")
	@NotNull
	protected Date date;

	@Column(name = "ACCOUNT_ID")
	@NotNull
	protected Integer accountId;

	@Column(name = "TYPE")
	@NotNull
	protected StockSaleProfit.Type type;

	@Column(name = "PROFIT")
	@NotNull
	protected Double profit;

	@Column(name = "NEGOTIATED")
	@NotNull
	protected Double negotiated;

	@Column(name = "INCOME_TAX")
	@NotNull
	protected Double incomeTax;

    @Column(name="USER_GROUP_ID")
    @NotNull
    protected Integer groupId;

	@Transient
	protected Double profitBalance;

	@Transient
	protected Double itBalance;

	public enum Type {
		PROFIT ( "Lucro" ), IR ( "Imposto" );
		private String name;

		Type(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
