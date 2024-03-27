/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.orm.test.property;

import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.spi.PropertyAccess;

import org.hibernate.property.access.spi.TypeIntrospectionHelper;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ebersole
 */
public class BasicPropertyAccessorTest extends BaseUnitTestCase {
	public static abstract class Super {
		public abstract Object getIt();
		public abstract void setIt(Object it);
	}

	public static class Duper extends Super {
		private String it;

		public Duper(String it) {
			this.it = it;
		}

		public String getIt() {
			return it;
		}

		@Override
		public void setIt(Object it) {
			this.it = ( it == null || String.class.isInstance( it ) )
					? (String) it
					: it.toString();
		}
	}

	public static class Duper2 extends Super {
		private String it;

		public Duper2(String it) {
			this.it = it;
		}

		public String getIt() {
			return it;
		}

		public void setIt(String it) {
			this.it = it;
		}

		@Override
		public void setIt(Object it) {
			if ( it == null || String.class.isInstance( it ) ) {
				setIt( (String) it );
			}
			else {
				setIt( it.toString() );
			}
		}
	}

	@Test
	public void testBridgeMethodDisregarded() {
		PropertyAccessStrategyBasicImpl accessStrategy = PropertyAccessStrategyBasicImpl.INSTANCE;
		{
			TypeIntrospectionHelper duperType = TypeIntrospectionHelper.fromType(Duper.class);
			final PropertyAccess access = accessStrategy.buildPropertyAccess( duperType, "it", true );
			assertEquals( String.class, access.getGetter().getReturnTypeClass() );
			assertEquals( Object.class, access.getSetter().getMethod().getParameterTypes()[0] );
		}

		{
			TypeIntrospectionHelper duperType2 = TypeIntrospectionHelper.fromType(Duper2.class);
			final PropertyAccess access = accessStrategy.buildPropertyAccess( duperType2, "it", true );
			assertEquals( String.class, access.getGetter().getReturnTypeClass() );
			assertEquals( String.class, access.getSetter().getMethod().getParameterTypes()[0] );
		}
	}
}
