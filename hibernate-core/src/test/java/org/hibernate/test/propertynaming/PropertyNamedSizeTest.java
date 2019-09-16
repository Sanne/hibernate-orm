/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.propertynaming;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Test;

public class PropertyNamedSizeTest extends BaseNonConfigCoreFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] { Cart.class, Items.class };
	}

	@Test
	public void testQueryHint() {
		{
			final Session session = openSession();
			session.beginTransaction();
			{
				final Cart cart1 = new Cart();
				cart1.setId( 1L );
				final Items item1 = new Items();
				item1.setId( 1L );
				item1.setSize( 1000 );
				item1.setCart( cart1 );

				final Items item2 = new Items();
				item2.setId( 2L );
				item2.setSize( 2000 );
				item2.setCart( cart1 );

				final List<Items> itemsSetForCar1 = new ArrayList<>();
				itemsSetForCar1.add( item1 );
				itemsSetForCar1.add( item2 );
				cart1.setItems( itemsSetForCar1 );
				session.persist( cart1 );
			}

			{
				final Cart cart2 = new Cart();
				cart2.setId( 2L );
				final Items item3 = new Items();
				item3.setId( 3L );
				item3.setSize( 3000 );
				item3.setCart( cart2 );

				final Items item4 = new Items();
				item4.setId( 4L );
				item4.setSize( 4000 );
				item4.setCart( cart2 );

				final List<Items> itemsSetForCar2 = new ArrayList<>();
				itemsSetForCar2.add( item3 );
				itemsSetForCar2.add( item4 );
				cart2.setItems( itemsSetForCar2 );
				session.persist( cart2 );
			}
			session.getTransaction().commit();
			session.close();
		}
		{
			final Session session = openSession();
			session.beginTransaction();
			final Cart cart1 = session.get( Cart.class, 1L );

			for ( Items items : cart1.getItems() ) {
				System.out.println( cart1.getId() + " item property size value = " + items.getSize() );
			}

			final Cart cart2  = session.get( Cart.class, 2L );
			for ( Items items : cart2.getItems() ) {
				System.out.println( cart2.getId() + " item property size value = " + items.getSize() );
			}

			Query query = session.createQuery( "select o, (o.items.size) as col from Cart o" );
			List list = query.list();
			session.getTransaction().commit();
			session.close();
		}
	}

}
