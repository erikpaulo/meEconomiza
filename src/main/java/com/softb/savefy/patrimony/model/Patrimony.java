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
        this.balanceInvested = 0.0;
        this.balance = 0.0;
        this.increasedBalance = 0.0;
        this.pctIncreasedBalance = 0.0;
        this.profit = 0.0;
        this.increasedProfit = 0.0;
        this.pctIncreasedProfit = 0.0;
        this.entries = new ArrayList<>();
        this.groupId = groupId;
    }

	@Column(name = "DATE")
	@NotNull
	private Date date;

	@Column(name = "BALANCE")
	@NotNull
	private Double balance;

	@Column(name = "BALANCE_INVESTED")
	@NotNull
    private Double balanceInvested;

	@Column(name = "INCREASED_BALANCE")
	@NotNull
	private Double increasedBalance;

	@Column(name = "PCT_INCREASED_BALANCE")
	@NotNull
	private Double pctIncreasedBalance;

	@Column(name = "PROFIT")
	@NotNull
	private Double profit;

	@Column(name = "INCREASED_PROFIT")
	@NotNull
	private Double increasedProfit;

	@Column(name = "PCT_INCREASED_PROFIT")
	@NotNull
	private Double pctIncreasedProfit;

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

    @Transient
    protected Map<Date, Benchmark> BenchmarkMap;

}
