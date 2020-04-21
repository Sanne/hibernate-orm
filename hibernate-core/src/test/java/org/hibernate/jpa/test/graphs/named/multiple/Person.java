/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs.named.multiple;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;

/**
 * @author Steve Ebersole
 */
@Entity(name = "Person")
@NamedEntityGraphs({
		@NamedEntityGraph( name = "abc" ),
		@NamedEntityGraph( name = "xyz" )
})
public class Person {
	@Id
	public Long id;
}
