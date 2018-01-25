package com.softb.savefy.account.web.resource;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Classe que representa as atualizações de preço de um determinado ativo.
 * @author Erik Lacerda
 *
 */
@Data
@NoArgsConstructor
public class AssetPrice implements Serializable {

	private static final long serialVersionUID = 1L;

    protected Integer accountId;
    protected Date date;
    protected Double value;
}
