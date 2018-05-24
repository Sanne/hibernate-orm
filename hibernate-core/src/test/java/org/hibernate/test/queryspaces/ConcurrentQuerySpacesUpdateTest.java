/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryspaces;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Query;

import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConcurrentQuerySpacesUpdateTest extends BaseEntityManagerFunctionalTestCase {

	private static String QUERY = "select count(*) from events";
	private static String QUERY_SPACE = "doNotFlush";
	private static String QUERY_NAME = "HibernateTest";
	private App testApp;

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Event.class };
	}

	@Before
	public void prepare() {
		testApp = new App( entityManagerFactory() );
		testApp.persist( new Event( "Event1" ) );
		testApp.registerNativeQueryWithoutFlush( QUERY, QUERY_NAME );
	}

	@After
	public void clean() {
		testApp.close();
	}

	@Test
	public void shouldNotFlushByDefault() {
		// We want to flush native query manually. Default behavior: don't flush
		Query query = testApp.getNamedQuery( QUERY_NAME);
		BigInteger intValue = (BigInteger) query.getSingleResult();
		// By default, testApp add query space to prevent flush
		assertEquals( 0, intValue.intValue() );
		assertEquals( QUERY_SPACE, testApp.getSynchronizedQuerySpace( query ).iterator().next() );
	}

	@Test
	public void shouldFlush() {
		Query query = testApp.getNamedQuery(QUERY_NAME);
		// Without Synchronized Query Space native query will provoke flush
		testApp.getSynchronizedQuerySpace(query).clear();
		BigInteger intValue = (BigInteger) query.getSingleResult();

		assertEquals(1, intValue.intValue());
	}

	@Test
	public void shouldDoItWithFlushAndNot() {
		final Query query1 = testApp.getNamedQuery(QUERY_NAME);
		final Query query2 = testApp.getNamedQuery(QUERY_NAME);

		// Start evil thread for modifying of Query 2
		new Thread(new Runnable() {
			@Override public void run() {
				while (true) {
					testApp.addSynchronizedQuerySpace(query2, String.valueOf(new Date().toString()));
				}
			}
		}).start();

		// Wait 1 second (we should accumulate some items in query spaces of query2)
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// SQLCustomQuery will be created based on our query.
		// QuerySpaces will be "copied" with Collection.addAll (which uses iteration over an query spaces of our query)
		// Unexpectedly, Query 1 query spaces modified Query 2 query spaces in an thread unsafe manner
		BigInteger intValue = (BigInteger) query1.getSingleResult();
	}

}
