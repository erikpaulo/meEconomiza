package com.softb.savefy.account.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@Data
@Entity
@DiscriminatorValue("BFA")
public class BenefitsAccount extends CheckingAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name="BENEFIT_DESCRIPTION")
	@NotEmpty
	protected String benefitDescription;
}
