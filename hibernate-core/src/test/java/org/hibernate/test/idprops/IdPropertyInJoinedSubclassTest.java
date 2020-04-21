/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.idprops;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInHibernate;
import static org.junit.Assert.assertEquals;

/**
 * @author Gail Badner
 */
public class IdPropertyInJoinedSubclassTest extends BaseCoreFunctionalTestCase {
	@Override
	public Class[] getAnnotatedClasses() {
		return new Class[] { Human.class, Genius.class };
	}

	@Before
	public void setUp() {
		doInHibernate( this::sessionFactory, session -> {
			session.persist( new Genius() );
			session.persist( new Genius( 1L ) );
			session.persist( new Genius( 1L ) );
		} );
	}

	@Test
	@TestForIssue(jiraKey = "HHH-13114")
	public void testHql() {
		doInHibernate( this::sessionFactory, session -> {
			assertEquals(
					2, session.createQuery( "from Genius g where g.id = :id", Genius.class )
							.setParameter( "id", 1L )
							.list()
							.size()
			);

			assertEquals(
					1, session.createQuery( "from Genius g where g.id is null", Genius.class )
							.list()
							.size()
			);

			assertEquals( 3L, session.createQuery( "select count( g ) from Genius g" ).uniqueResult() );

			assertEquals(
					2, session.createQuery( "from Human h where h.id = :id", Human.class )
							.setParameter( "id", 1L )
							.list()
							.size()
			);

			assertEquals(
					1, session.createQuery( "from Human h where h.id is null", Human.class )
							.list()
							.size()
			);

			assertEquals( 3L, session.createQuery( "select count( h ) from Human h" ).uniqueResult() );
		} );
	}

	@Entity(name = "Human")
	@Inheritance(strategy = InheritanceType.JOINED)
	public static class Human {

		private Long realId;

		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		@Column(name = "realId")
		public Long getRealId() {
			return realId;
		}

		public void setRealId(Long realId) {
			this.realId = realId;
		}
	}

	@Entity(name = "Genius")
	public static class Genius extends Human {
		private Long id;

		public Genius() {
		}

		public Genius(Long id) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

	}
}
