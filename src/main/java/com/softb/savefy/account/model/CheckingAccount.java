package com.softb.savefy.account.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@Data
@Entity
@DiscriminatorValue("CKA")
public class CheckingAccount extends Account implements Serializable {

	private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<CheckingAccountEntry> entries;

    @Column(name="START_BALANCE")
    @NotNull
    protected Double startBalance;
}
