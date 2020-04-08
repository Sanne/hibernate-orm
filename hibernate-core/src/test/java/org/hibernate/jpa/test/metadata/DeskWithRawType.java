/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.metadata;
import java.util.Collection;
import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

/**
 * @author Emmanuel Bernard
 */
@Entity
public class DeskWithRawType implements java.io.Serializable {

	@Id
	protected String id;

	@Basic
	protected String name;

	public DeskWithRawType() {
	}

	@ManyToMany(targetEntity = EmployeeWithRawType.class, cascade = CascadeType.ALL)
	@JoinTable(name = "DESK_EMPL",
			joinColumns =
			@JoinColumn(
					name = "DESK_FK", referencedColumnName = "ID"),
			inverseJoinColumns =
			@JoinColumn(
					name = "EMPL_FK", referencedColumnName = "ID")
	)
	protected Collection employees = new java.util.ArrayList();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection getEmployees() {
		return employees;
	}

	public void setEmployees(Collection employees) {
		this.employees = employees;
	}
}
