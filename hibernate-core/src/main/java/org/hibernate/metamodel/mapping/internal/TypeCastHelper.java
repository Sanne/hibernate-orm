/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.mapping.internal;

import org.hibernate.metamodel.mapping.JdbcMappingContainer;
import org.hibernate.metamodel.mapping.SqlExpressible;
import org.hibernate.type.internal.NamedBasicTypeImpl;

public final class TypeCastHelper {

	public static SqlExpressible toSqlExpressible(final JdbcMappingContainer t) {
		if ( t instanceof NamedBasicTypeImpl ) {
			return (NamedBasicTypeImpl) t;
		}
		else if ( t instanceof BasicAttributeMapping ) {
			return (BasicAttributeMapping) t;
		}
		else {
			return (SqlExpressible) t;
		}
	}

	public static boolean isSqlExpressible(final JdbcMappingContainer t) {
		if ( t instanceof NamedBasicTypeImpl ) {
			return true;
		}
		else if ( t instanceof BasicAttributeMapping ) {
			return true;
		}
		else {
			return ( t instanceof SqlExpressible );
		}
	}

}
