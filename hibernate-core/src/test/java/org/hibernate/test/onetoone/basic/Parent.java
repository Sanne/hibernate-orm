/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.onetoone.basic;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

/**
 * @author Florian Rampp
 * @author Steve Ebersole
 */
@Entity
public class Parent {

	@Id
	Long id;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "parent")
	Child child;

	void setChild(Child child) {
		this.child = child;
		child.setParent(this);
	}

}
