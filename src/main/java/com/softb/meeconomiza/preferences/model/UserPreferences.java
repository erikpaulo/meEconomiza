package com.softb.meeconomiza.preferences.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Classe que representa as preferências do usuário.
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "USER_PREFERENCES")
public class UserPreferences extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "UPDATE_INSTALLMENT_DATE")
	protected Boolean updateInstallmentDate;

	@Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

}
