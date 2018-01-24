package com.softb.savefy.patrimony.model;

import com.softb.savefy.account.model.Account;
import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que armazena o total possúido em cada conta
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PATRIMONY_ENTRY")
public class PatrimonyEntry extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "NAME")
	@NotEmpty
	protected String name;

	@Column(name = "DATE")
	@NotNull
	protected Date date;

	@Column(name = "ASSET_TYPE")
	@NotNull
	protected Account.Type assetType;

    @Column(name = "ACCOUNT_ID")
    @NotNull
    protected Integer accountId;

	@Column(name = "BALANCE")
	@NotNull
	protected Double balance;

    @Column(name="INCREASED_BALANCE")
    @NotNull
    protected Double increasedBalance;

    @Column(name="PCT_INCREASED_BALANCE")
    @NotNull
    protected Double percentIncreasedBalance;

    @Column(name="PROFIT")
    @NotNull
    protected Double profit;

    @Column(name="INCREASED_PROFIT")
    @NotNull
    protected Double increasedProfit;

    @Column(name="PCT_INCREASED_PROFIT")
    @NotNull
    protected Double pctIncreasedProfit;

    @Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

	@Transient
	protected String institution;

	@Transient
	protected List<PatrimonyEntry> history;
}
