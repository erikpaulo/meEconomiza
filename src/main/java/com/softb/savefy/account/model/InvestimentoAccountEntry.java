package com.softb.savefy.account.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

/**
 * Classe que representa as entradas de um investimento.
 * @author Erik Lacerda
 *
 */
@Data
@Entity
@DiscriminatorValue("CCA")
@JsonIgnoreProperties(ignoreUnknown = true)
public class InvestimentoAccountEntry extends AccountEntry implements Serializable {

	private static final long serialVersionUID = 1L;
}
