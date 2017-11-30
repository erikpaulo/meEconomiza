package com.softb.savefy.categorization.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Represents the relation between an expense's description with its subcategory
 * @author Erik Lacerda
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "CATEGORY_PREDICTION")
public class CategoryPrediction extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "DESCRIPTION")
	@NotEmpty
	protected String description;

	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SUBCATEGORY_ID", referencedColumnName = "ID")
	protected SubCategory subCategory;

	@Column(name = "TIMES_USED")
	@NotNull
	protected Integer timesUsed;

	@Column(name = "TIMES_REJECTED")
	@NotNull
	protected Integer timesRejected;

    @Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

}
