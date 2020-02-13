/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot;

import java.util.Collections;
import java.util.Map;

import org.hibernate.boot.starterdsl.HibernateStarterConfiguration;
import org.hibernate.boot.starterdsl.HibernateStarterConfigurationImpl;

public final class SimplifiedStarter {

	public static HibernateStarterConfiguration create() {
		return new HibernateStarterConfigurationImpl( Collections.emptyMap() );
	}

	public static HibernateStarterConfiguration create(final Map<String, Object> initialConfigurationSettings) {
		return new HibernateStarterConfigurationImpl( initialConfigurationSettings );
	}

	public static void main(String[] args) {
		SimplifiedStarter.create()
				.setProperty( "one", "value" )
				.setProperty( "two", new Object() )
				.startSessionFactory();
	}

}
