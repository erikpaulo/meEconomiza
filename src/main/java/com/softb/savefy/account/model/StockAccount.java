package com.softb.savefy.account.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Classe que representa um portifólio de ações mantido pelo usuário
 * @author Erik Lacerda
 *
 */
@Data
@Entity
@DiscriminatorValue("STK")
public class StockAccount extends Account implements Serializable {

	private static final long serialVersionUID = 1L;

    @Column(name="RISK")
    @NotEmpty
    protected String risk = "HIGH";

    // D+ OR DUE DATE
    @Column(name="LIQUIDITY_TYPE")
    @NotNull
    protected InvestmentAccount.LiquidityType liquidityType = InvestmentAccount.LiquidityType.DPLUS;

    // IF D+, DEFINE HOW MANY DAYS GET THE MONEY
    @Column(name="LIQUIDITY_DAYS")
    protected Double liquidityDays = 3.0;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<StockAccountEntry> stocks;

}
