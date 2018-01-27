package com.softb.savefy.patrimony.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as Contas bancárias do usuário
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

	@Column(name = "DATE")
	@NotNull
	protected Date date;

	@Column(name = "CDI")
	@NotNull
	protected Double cdi;

    @Column(name="IBOVESPA")
    @NotNull
    protected Double iBovespa;

}
