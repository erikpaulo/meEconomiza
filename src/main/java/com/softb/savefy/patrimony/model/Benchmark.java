package com.softb.savefy.patrimony.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa o patrimônio total do user
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "BENCHMARK")
public class Benchmark extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Transient
	private Date date;

	@Transient
	private Double ipca;

	@Transient
	private Double cdi;

	@Transient
	private Double ibovespa;

	@Transient
	private List<Benchmark> entries;
}
