package com.softb.savefy.patrimony.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Classe que representa o patrimônio total do user
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "PATRIMONY")
public class Patrimony extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	public Patrimony(Date date, Integer groupId){
        this.date = date;
        this.balanceTotal = 0.0;
        this.balanceInvested = 0.0;
        this.profitTotal = 0.0;
        this.profitInvested = 0.0;
        this.percentProfitTotal = 0.0;
        this.percentProfitInvested = 0.0;
        this.entries = new ArrayList<>();
        this.groupId = groupId;
    }

	@Column(name = "DATE")
	@NotNull
	private Date date;

	@Column(name = "BALANCE_TOTAL")
	@NotNull
	private Double balanceTotal;

	@Column(name = "BALANCE_INVESTED")
	@NotNull
    private Double balanceInvested;

	@Column(name = "PROFIT_TOTAL")
	@NotNull
	private Double profitTotal;

	@Column(name = "PROFIT_INVESTED")
	@NotNull
	private Double profitInvested;

	@Column(name = "PERCENT_PROFIT_TOTAL")
	@NotNull
	private Double percentProfitTotal;

	@Column(name = "PERCENT_PROFIT_INVESTED")
	@NotNull
	private Double percentProfitInvested;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "PATRIMONY_ID", referencedColumnName = "ID")
	protected List<PatrimonyEntry> entries;

	@Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

	@Transient
	protected List<Patrimony> history;

    @Transient
    protected Map<String, Double> riskMap;

    @Transient
    protected Map<Date, Double> liquidityMap;

    @Transient
    protected Map<String, Double> investTypeMap;

}
