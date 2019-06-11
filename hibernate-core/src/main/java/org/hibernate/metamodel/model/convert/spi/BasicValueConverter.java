/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.convert.spi;

import org.hibernate.Incubating;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

/**
 * Support for basic-value conversions.
 *
 * Conversions might be defined by:
 *
 * 		* a custom JPA {@link javax.persistence.AttributeConverter},
 * 		* implicitly, based on the Java type (e.g., enums)
 * 	    * etc
 *
 * @param <O> The Java type we can use to represent the domain (object) type
 * @param <R> The Java type we can use to represent the relational type
 *
 * @author Steve Ebersole
 */
@Incubating
public interface BasicValueConverter<O,R> {
	/**
	 * Convert the relational form just retrieved from JDBC ResultSet into
	 * the domain form.
	 */
	O toDomainValue(R relationalForm);

	/**
	 * Convert the domain form into the relational form in preparation for
	 * storage into JDBC
	 */
	R toRelationalValue(O domainForm);

	/**
	 * Descriptor for the Java type for the domain portion of this converter
	 */
	JavaTypeDescriptor<O> getDomainJavaDescriptor();

	/**
	 * Descriptor for the Java type for the relational portion of this converter
	 */
	JavaTypeDescriptor<R> getRelationalJavaDescriptor();
}
