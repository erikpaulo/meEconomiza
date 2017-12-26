package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "ACCOUNT_ENTRY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name= "TYPE")
public class AccountEntry extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

    @Column(name = "DATE")
    @NotNull
    protected Date date;

    @Column(name = "AMOUNT")
    @NotNull
    protected Double amount;

    @Column(name = "TYPE", insertable = false, updatable = false, nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    protected Account.Type type;

    @Column(name = "ACCOUNT_ID")
    @NotNull
    protected Integer accountId;

    @Column(name="USER_GROUP_ID")
    @NotNull
    protected Integer groupId;
}
