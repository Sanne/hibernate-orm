/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.sequence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.Map;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQL10Dialect;
import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;

import org.hibernate.testing.RequiresDialect;
import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;
import static org.junit.Assert.fail;

/**
 * @author Vlad Mhalcea
 */
@RequiresDialect(jiraKey = "HHH-13106", value = PostgreSQL10Dialect.class)
public class PostgreSQLIdentitySequenceTest extends BaseEntityManagerFunctionalTestCase {

	@Override
	protected Class[] getAnnotatedClasses() {
		return new Class[] { Role.class };
	}

	private DriverManagerConnectionProviderImpl connectionProvider;

	@Override
	public void buildEntityManagerFactory() {
		connectionProvider = new DriverManagerConnectionProviderImpl();
		connectionProvider.configure( Environment.getProperties() );

		try(Connection connection = connectionProvider.getConnection();
			Statement statement = connection.createStatement()) {
			statement.execute( "DROP TABLE IF EXISTS roles CASCADE" );
			statement.execute( "CREATE TABLE roles ( id BIGINT NOT NULL PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY )" );
		}
		catch (SQLException e) {
			fail(e.getMessage());
		}

		super.buildEntityManagerFactory();
	}

	@Override
	public void releaseResources() {
		super.releaseResources();

		try(Connection connection = connectionProvider.getConnection();
			Statement statement = connection.createStatement()) {
			statement.execute( "DROP TABLE IF EXISTS roles CASCADE" );
		}
		catch (SQLException e) {
			fail(e.getMessage());
		}

		if ( connectionProvider != null ) {
			connectionProvider.stop();
		}
	}

	@Test
	public void test() {
		doInJPA( this::entityManagerFactory, entityManager -> {
			Role role = new Role();
			entityManager.persist( role );
		} );
	}

	@Entity(name = "Role")
	public static class Role {

		@Id
		@Column(name = "id")
		@SequenceGenerator(name = "roles_id_seq", sequenceName = "roles_id_seq", allocationSize = 1)
		@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_id_seq")
		private Long id;

		public Long getId() {
			return id;
		}

		public void setId(final Long id) {
			this.id = id;
		}
	}

}
