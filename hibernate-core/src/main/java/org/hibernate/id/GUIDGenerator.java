/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

/**
 * Generates <tt>string</tt> values using the SQL Server NEWID() function.
 *
 * @author Joseph Fifield
 */
public class GUIDGenerator implements IdentifierGenerator {
	private static final CoreMessageLogger LOG = CoreLogging.messageLogger( GUIDGenerator.class );

	private static boolean WARNED;

	public GUIDGenerator() {
		if ( !WARNED ) {
			WARNED = true;
			LOG.deprecatedUuidGenerator( UUIDGenerator.class.getName(), UUIDGenerationStrategy.class.getName() );
		}
	}

	public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
		final String sql = session.getJdbcServices().getJdbcEnvironment().getDialect().getSelectGUIDString();
		final JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
		try {
			PreparedStatement st = jdbcCoordinator.getStatementPreparer().prepareStatement( sql );
			try {
				ResultSet rs = jdbcCoordinator.getResultSetReturn().extract( st );
				final String result;
				if ( !rs.next() ) {
					throw new HibernateException( "The database returned no GUID identity value" );
				}
				result = rs.getString( 1 );
				LOG.guidGenerated( result );
				return result;
			}
			finally {
				jdbcCoordinator.getLogicalConnection().getResourceRegistry().release( st );
				jdbcCoordinator.afterStatementExecution();
			}
		}
		catch (SQLException sqle) {
			throw session.getJdbcServices().getSqlExceptionHelper().convert(
					sqle,
					"could not retrieve GUID",
					sql
			);
		}
	}
}
