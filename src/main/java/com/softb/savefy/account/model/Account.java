package com.softb.savefy.account.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Classe que representa as Contas bancárias do usuário
 * @author Erik Lacerda 
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ACCOUNT")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name= "TYPE")
public class Account extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "NAME")
	@NotEmpty
	protected String name;

	@Column(name = "INSTITUTION")
	@NotNull
	protected Integer institutionId;

	@Column(name = "TYPE", insertable = false, updatable = false, nullable = false)
	@NotNull
	@Enumerated(EnumType.STRING)
	protected Type type;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ID")
    protected List<Conciliation> conciliations;

    @Column(name="ACTIVATED")
    @NotNull
    protected Boolean activated;

    @Column(name="USER_GROUP_ID")
	@NotNull
	protected Integer groupId;

    @Column(name="LAST_UPDATE")
    @NotNull
    protected Date lastUpdate;

	@Transient
    protected Double balance;

    public enum Type {
        CKA ( "Conta Corrente" ), INV ( "Conta Investimento" ), CCA ( "Cartão de Crédito" );
        private String name;

        Type(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
