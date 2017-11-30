package com.softb.savefy.account.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa uma conciliação associada a uma conta.
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "CONCILIATION")
public class Conciliation extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "DATE")
	@NotNull
	protected Date date;

	@Column(name = "ACCOUNT_ID")
	@NotNull
	protected Integer accountId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "CONCILIATION_ID", referencedColumnName = "ID")
    protected List<ConciliationEntry> entries;

    @Column(name="IMPORTED")
    @NotNull
    protected Boolean imported;

    @Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

}
