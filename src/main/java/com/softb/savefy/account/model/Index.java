package com.softb.savefy.account.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as atualizações do indexador de um investimento.
 * @author Erik Lacerda
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "INDEX")
public class Index extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

    @Column(name = "ACCOUNT_ID")
    @NotNull
    protected Integer accountId;

    @Column(name = "DATE")
    @NotNull
    protected Date date;

    @Column(name = "VALUE")
    @NotNull
    protected Double value;

    @Column(name = "USER_GROUP_ID")
    @NotNull
    protected Integer groupId;
}
