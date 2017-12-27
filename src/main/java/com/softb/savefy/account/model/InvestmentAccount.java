package com.softb.savefy.account.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa uma conta de investimentos. Existem tipos de investimentos diferentes de acordo com
 * cada produto que se destina seus patrimônios
 * @author Erik Lacerda 
 *
 */
@Data
@Entity
@DiscriminatorValue("INV")
public class InvestmentAccount extends Account implements Serializable {

	private static final long serialVersionUID = 1L;

    @Column(name="PRODUCT")
    @NotNull
    protected Product product;

    @Column(name="ADMIN_TAX")
    @NotNull
    protected Double adminTax;

    @Column(name="RISK")
    @NotEmpty
    protected String risk;

    // D+ OR DUE DATE
    @Column(name="LIQUIDITY_TYPE")
    @NotNull
    protected LiquidityType liquidityType;

    // IF D+, DEFINE HOW MANY DAYS GET THE MONEY
    @Column(name="LIQUIDITY_DAYS")
    protected Double liquidityDays;

    // IF DUE DATE, DEFINE THE DAY TO GET FULL REMUNERATION
    @Column(name="LIQUIDITY_DUE_DATE")
    protected Date liquidityDueDate;

    // DEFINE THE DATE, INDEPENDENT OF THE LIQUIDITY TYPE
    @Transient
    protected Double liquidityDate;

    // DEFINE THE DATE, INDEPENDENT OF THE LIQUIDITY TYPE
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<Index> indexValues;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<InvestmentAccountEntry> entries;

    public enum Product {
        INVESTMENT_FUND ( "Fundo de Investimentos" ), INVESTMENT_FUND_OF_SHARES ( "Fundo de Acões" ), INVESTMENT_FUND_PENSION ( "Fundo de Previdência" );
        private String name;

        Product(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum LiquidityType {
        DPLUS ( "D+" ), DUE_DATE ( "Vencimento" );
        private String name;

        LiquidityType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
