package org.hibernate.test.criteria;

import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public class CriteriaLockingTest extends BaseCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class[] { Bid.class, Item.class };
	}

	//TODO assert the log isn't being written - https://hibernate.atlassian.net/browse/HHH-8788


	@Test
	@TestForIssue(jiraKey = "HHH-8788")
	public void testCriteriaOrderBy() {
		final Session s = openSession();
		final Transaction tx = s.beginTransaction();

		Item item = new Item();
		item.name = "ZZZZ";
		s.persist( item );

		s.flush();

		Criteria criteria = s.createCriteria( Item.class )
				.setLockMode( LockMode.NONE );

		criteria.list();

		tx.rollback();
		s.close();
	}

}
