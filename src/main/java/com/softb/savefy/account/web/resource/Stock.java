package com.softb.savefy.account.web.resource;

import com.softb.savefy.account.model.StockAccountEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Classe que representa uma operação de compras e/ou vendas na bolsa.
 * @author Erik Lacerda
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {

	private static final long serialVersionUID = 1L;

    protected StockAccountEntry.Operation operation;
    protected String code;
    protected Integer quantity;
    protected Double originalPrice;
    protected Double brokerage;

}
