package com.softb.savefy.account.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Esta classe mapeia a relação de venda de cotas em um fundo de investimentos. Um resgate, pode precisar
 * vender cotas de várias aplicações diferentes.
 * @author Erik Lacerda
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "QUOTE_SALE")
public class QuoteSale extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PURCHASE_ENTRY_ID", referencedColumnName = "ID")
    protected InvestmentAccountEntry purchaseEntry;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SALE_ENTRY_ID", referencedColumnName = "ID")
    protected InvestmentAccountEntry saleEntry;

    @Column(name = "QTD_QUOTES")
    @NotNull
    protected Double qtdQuotes;
}
