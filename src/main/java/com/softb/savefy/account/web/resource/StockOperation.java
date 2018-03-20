package com.softb.savefy.account.web.resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa uma operação de compras e/ou vendas na bolsa.
 * @author Erik Lacerda
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOperation implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Integer accountId;
    protected Date date;
    protected List<Stock> assets;
    protected String type;

}
