/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.domain;

import org.hibernate.Incubating;
import org.hibernate.metamodel.model.mapping.spi.Writeable;
import org.hibernate.query.Query;

/**
 * Specialization of DomainType for types that can be used as {@link Query} parameter bind values
 *
 * todo (6.0) : extend Writeable (and therefore Readable too)?  or composition?
 *
 * @author Steve Ebersole
 */
@Incubating
public interface AllowableParameterType<J> extends Writeable {
}
