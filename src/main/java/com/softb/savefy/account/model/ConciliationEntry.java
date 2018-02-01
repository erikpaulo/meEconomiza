package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.softb.savefy.categorization.model.SubCategory;
import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as entradas em uma conciliação.
 * @author Erik Lacerda
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "CONCILIATION_ENTRY")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConciliationEntry extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "DATE")
	@NotNull
	protected Date date;

	@Column(name = "DESCRIPTION")
	@NotEmpty
	protected String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SUBCATEGORY_ID", referencedColumnName = "ID")
    protected SubCategory subCategory;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "ACCOUNT_ENTRY_ID", referencedColumnName = "ID")
    protected CheckingAccountEntry accountEntry;

	@Column(name = "AMOUNT")
	@NotNull
	protected Double amount;

	@Column(name = "REJECT")
	@NotNull
	protected Boolean reject;

	@Transient
    protected Boolean exists;

	@Transient
    protected Boolean installment;

	@Column(name = "CONCILIATION_ID")
	protected Integer conciliationId;

	@Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;
}
