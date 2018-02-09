package com.softb.savefy.account.model;

import com.softb.savefy.categorization.model.SubCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as entradas em uma conta de usuário.
 * @author Erik Lacerda
 *
 */
@Data
@Entity
@NoArgsConstructor
@DiscriminatorValue("BFA")
public class BenefitsAccountEntry extends CheckingAccountEntry implements Serializable {

	private static final long serialVersionUID = 1L;


	public BenefitsAccountEntry(Date date, SubCategory subCategory, Double amount, Boolean transfer, Integer accountId,
                                Integer accountDestinyId, Integer twinEntryId, Integer groupId, Double balance, Account.Type type) {
//		super.date = date;
//		super.amount = amount;
//		super.accountId = accountId;
//		super.groupId = groupId;
//		this.subCategory = subCategory;
//		this.transfer = transfer;
//		this.accountDestinyId = accountDestinyId;
//		this.twinEntryId = twinEntryId;
//		this.balance = balance;
//		super.type = type;
		super(date, subCategory, amount, transfer, accountId, accountDestinyId, twinEntryId, groupId, balance, type);
	}

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "SUBCATEGORY_ID", referencedColumnName = "ID")
//    protected SubCategory subCategory;
//
//	@Column(name = "TRANSFER")
//	@NotNull
//    @ColumnDefault( value="false" )
//	protected Boolean transfer;
//
//    @Column(name = "ACCOUNT_DESTINY_ID")
//	protected Integer accountDestinyId;
//
//	@Column(name = "TWIN_ENTRY_ID")
//	protected Integer twinEntryId;
//
//	@Transient
//	public Double getBalance() {
//		return balance;
//	}
//
//	@Transient
//    protected Double balance;
//
	@Override
    public BenefitsAccountEntry clone() throws CloneNotSupportedException {
        return new BenefitsAccountEntry( super.date,      this.subCategory, this.amount, this.transfer,
                                 this.accountId, this.accountDestinyId, this.twinEntryId, this.groupId,
                                 this.balance, this.type);
    }
}
