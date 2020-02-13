package org.hibernate.test.simplifiedstart;

import org.hibernate.SessionFactory;
import org.hibernate.boot.SimplifiedStarter;

import org.junit.Assert;
import org.junit.Test;

public class SimplifiedStartTest {

	@Test
	public void testBootstrapOnH2() {
		SessionFactory sessionFactory = SimplifiedStarter.create()
				.setProperty( "hibernate.connection.driver_class", "org.h2.Driver" )
				.setProperty( "hibernate.dialect", "org.hibernate.dialect.H2Dialect" )
				.setProperty( "hibernate.connection.url", "jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1" )
				.setProperty( "hibernate.connection.username", "sa" )
				.setProperty( "hibernate.connection.password", "" )
				.setProperty( "hibernate.hbm2ddl.auto", "create-drop" )
				.setProperty( "anything else", new Object() )
				.startSessionFactory();
		Assert.assertNotNull( sessionFactory );
		try {
			testFunctionality( sessionFactory );
		}
		finally {
			sessionFactory.close();
		}
	}

	private void testFunctionality(final SessionFactory sessionFactory) {
	}
}
