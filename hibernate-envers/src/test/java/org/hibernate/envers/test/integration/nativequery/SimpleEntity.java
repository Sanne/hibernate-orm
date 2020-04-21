/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.nativequery;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import org.hibernate.envers.Audited;

/**
 * @author Andrea Boriero
 */
@Entity(name = "SimpleEntity")
@Audited
public class SimpleEntity {
	@Id
	@GeneratedValue
	private Long id;

	private String stringField;

	public SimpleEntity() {
	}

	public SimpleEntity(String stringField) {
		this.stringField = stringField;
	}

	public Long getId() {
		return id;
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
}

