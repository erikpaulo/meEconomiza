package com.softb.meeconomiza.categorization.model;

import com.softb.system.repository.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Represents a set of patterns that are applied to descriptions to make it more predicted
 * @author Erik Lacerda
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "SANITIZE_PATTERN")
public class SanitizePattern extends BaseEntity<Integer> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "PATTERN")
	@NotEmpty
	protected String pattern;

	@Column(name = "replaceFor")
	@NotNull
	protected String replaceFor;

}
