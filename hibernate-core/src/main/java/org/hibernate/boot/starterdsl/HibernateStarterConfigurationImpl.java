/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.starterdsl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public final class HibernateStarterConfigurationImpl implements HibernateStarterConfiguration {

	private final Map<String, Object> configuration = new HashMap<>();

	public HibernateStarterConfigurationImpl(final Map<String, Object> initialConfigurationSettings) {
		configuration.putAll( initialConfigurationSettings );
	}

	@Override
	public HibernateStarterConfiguration setProperty(final String key, final Object value) {
		configuration.put( key, value );
		return this;
	}

	@Override
	public SessionFactory startSessionFactory() {
		final BootstrapServiceRegistry bootRegistry = buildBootstrapServiceRegistry();
		final Configuration configuration = buildConfiguration();
		final StandardServiceRegistry serviceRegistry = buildServiceRegistry(
				bootRegistry,
				configuration
		);
		return configuration.buildSessionFactory( serviceRegistry );
	}

	protected BootstrapServiceRegistry buildBootstrapServiceRegistry() {
		final BootstrapServiceRegistryBuilder builder = new BootstrapServiceRegistryBuilder();
		builder.applyClassLoaderService( SimplestClassLoaderService.INSTANCE );
		return builder.build();
	}

	protected Configuration buildConfiguration() {
		Properties p = new Properties();
		p.putAll( this.configuration );
		Configuration cfg = new Configuration();
		cfg.setProperties( p );
		return cfg;
	}

	protected StandardServiceRegistry buildServiceRegistry(BootstrapServiceRegistry bootRegistry, Configuration configuration) {
		StandardServiceRegistryBuilder cfgRegistryBuilder = configuration.getStandardServiceRegistryBuilder();

		StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder( bootRegistry, cfgRegistryBuilder.getAggregatedCfgXml() )
				.applySettings( configuration.getProperties() );

		return registryBuilder.build();
	}

}
