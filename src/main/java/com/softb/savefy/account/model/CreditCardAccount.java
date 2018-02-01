package com.softb.savefy.account.model;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@Data
@Entity
@DiscriminatorValue("CCA")
public class CreditCardAccount extends CheckingAccount implements Serializable {

	private static final long serialVersionUID = 1L;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
//    protected List<CreditCardAccount> entries;
//
//    @Column(name="START_BALANCE")
//    @NotNull
//    protected Double startBalance;
//
//    @Override
//    @JsonIgnore
//    public Date getLiquidityDate() {
//        Calendar cal = Calendar.getInstance();
//        return AppDate.getMonthDate(cal.getTime());
//    }
}
