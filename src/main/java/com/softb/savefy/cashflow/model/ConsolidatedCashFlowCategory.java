package com.softb.savefy.cashflow.model;

import com.softb.system.repository.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * Classe que representa o fluxo de entradas e saídas consolidado por subcategoria
 * @author Erik Lacerda 
 *
 */
//@AllArgsConstructor
//@NoArgsConstructor
@Data
public class ConsolidatedCashFlowCategory extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

//	protected Category category;
//
//	private List<Double> perMonthValue;
//	private List<Double> perMonthAverage;
//	private List<Double> perMonthVariation;
//
//	protected List<ConsolidatedCashFlowCategory> subCategories;
}
