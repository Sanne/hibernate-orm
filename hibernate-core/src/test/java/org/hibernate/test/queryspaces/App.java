/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.queryspaces;

import java.util.Collection;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Query;

import org.hibernate.SQLQuery;

public class App {

	private final EntityManagerFactory emf;
	private final EntityManager em;

	public App(EntityManagerFactory emf) {
		this.emf = emf;
		this.em = emf.createEntityManager();
		em.setFlushMode( FlushModeType.AUTO );
	}

	public void close() {
		em.close();
	}

	/**
	 * Save entity in database
	 * @param event
	 */
	public void persist(Event event) {
		em.getTransaction().begin();
		em.persist(event);
	}

	/**
	 * Register Native Query with query space
	 * @param nativeQuery
	 * @param namedQueryName
	 */
	public void registerNativeQueryWithoutFlush(String nativeQuery, String namedQueryName) {
		Query query = em.createNativeQuery( nativeQuery );
		addSynchronizedQuerySpace( query, "doNotFlush" );
		emf.addNamedQuery( namedQueryName, query );
	}

	public Query getNamedQuery( String queryName ) {
		Query basicQuery = em.createNamedQuery( queryName );
		return basicQuery;
	}

	public void addSynchronizedQuerySpace(Query query, String querySpace) {
		org.hibernate.SQLQuery unwrappedQuery = query.unwrap( SQLQuery.class );
		unwrappedQuery.addSynchronizedQuerySpace( querySpace );
	}

	public Collection<String> getSynchronizedQuerySpace(Query query) {
		org.hibernate.SQLQuery unwrappedQuery = query.unwrap( SQLQuery.class );
		return unwrappedQuery.getSynchronizedQuerySpaces();
	}

	public void commit() {
		em.getTransaction().commit();
	}

}
