package com.softb.savefy.account.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
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
public class InvestimentAccount extends Account implements Serializable {

	private static final long serialVersionUID = 1L;

    @Column(name="PRODUCT")
    @NotNull
    protected Product product;

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
}
