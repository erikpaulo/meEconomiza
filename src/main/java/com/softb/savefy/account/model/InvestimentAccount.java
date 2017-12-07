package com.softb.savefy.account.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@Data
@Entity
@DiscriminatorValue("CCA")
public class InvestimentAccount extends Account implements Serializable {

	private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<CheckingAccountEntry> entries;
}
