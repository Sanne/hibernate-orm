/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.classcache;

import java.util.Map;

import org.hibernate.bytecode.internal.SessionFactoryObserverForBytecodeEnhancer;
import org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl;
import org.hibernate.bytecode.spi.BasicProxyFactory;
import org.hibernate.proxy.ProxyConfiguration;

import org.hibernate.test.bytecode.Bean;
import org.hibernate.test.id.Car;
import org.junit.Assert;
import org.junit.Test;

public class MetadataLeakTest {

	@Test
	public void classMetadataReuse() {
		OrmLifecycleMock orm1 = new OrmLifecycleMock();

		//Two proxies of the same class should share the same Class, but be different instances:
		Object beanProxy1 = orm1.createBasicProxy( Bean.class );
		Object beanProxy2 = orm1.createBasicProxy( Bean.class );
		Assert.assertNotSame( beanProxy1, beanProxy2 );
		Assert.assertSame( beanProxy1.getClass(), beanProxy2.getClass() );
		//We now made the proxy name stable (predictable) by removing the random postfix:
		Assert.assertEquals( "org.hibernate.test.bytecode.Bean$HibernateBasicProxy", beanProxy1.getClass().getName() );

		//Of course a proxy of a different type should not share the Class (obvious?):
		Object carProxy1 = orm1.createBasicProxy( Car.class );
		Assert.assertNotSame( beanProxy1, carProxy1 );
		Assert.assertNotSame( beanProxy1.getClass(), carProxy1.getClass() );
		Assert.assertEquals( "org.hibernate.test.id.Car$HibernateBasicProxy", carProxy1.getClass().getName() );

		//The above is assumed to have been run during SessionFactory creation (before start);
		// now let's start orm1 :
		orm1.transitionToStarted();


		Object beanProxy3 = orm1.createBasicProxy( Bean.class );
		Assert.assertNotSame( beanProxy1, beanProxy3 );
		//Should still be able to reuse the same class definition:
		Assert.assertSame( beanProxy1.getClass(), beanProxy3.getClass() );

		// Now with a different ORM instance
		OrmLifecycleMock orm2 = new OrmLifecycleMock();
		Object beanProxy4 = orm2.createBasicProxy( Bean.class );
		Assert.assertSame( beanProxy1.getClass(), beanProxy4.getClass() );

		//shut them both down:
		orm1.transitionToStopped();
		orm2.transitionToStopped();

		//Third ORM instance, started after the other ones already stopped:
		OrmLifecycleMock orm3 = new OrmLifecycleMock();
		Object beanProxy5 = orm3.createBasicProxy( Bean.class );

		//Still reusing the same class definition:
		Assert.assertSame( beanProxy1.getClass(), beanProxy5.getClass() );

	}

	@Test
	public void metadataLeak() {

		OrmLifecycleMock orm1 = new OrmLifecycleMock();
//		JavaSE
		for (int i=0; i<1000000; i++) {

			Object proxy1 = orm1.createBasicProxy( Bean.class );
			orm1.transitionToStarted();
			Object proxy2 = orm1.createBasicProxy( Bean.class );
			Assert.assertSame( proxy1.getClass(), proxy2.getClass() );
		}
	}
/*
	public void classMultiplePUReuse() {

		SharedMetadata s = new SharedMetadata();

		OrmLifecycleMock orm1 = new OrmLifecycleMock();
		orm1.transitionToStarted(); // Bean.class

		OrmLifecycleMock orm2 = new OrmLifecycleMock( s );
		orm2.transitionToStarted(); // Car.classIdentifierHelperBuilde

		//,,.
		OrmLifecycleMock ormN = new OrmLifecycleMock( s );
		ormN.transitionToStarted(); // Animals.class

		s.nukeCaches();
//		orm1.reallyClearCaches();
//		orm2.reallyClearCaches();
//		// ...
//		ormN.reallyClearCaches();

	}*/

	private static class OrmLifecycleMock {
		final BytecodeProviderImpl bytecodeProvider = new BytecodeProviderImpl();
		final SessionFactoryObserverForBytecodeEnhancer sessionFactoryLifecycle = new SessionFactoryObserverForBytecodeEnhancer( bytecodeProvider );
//
//		final ClassValue proxyCache = new ClassValue() {
//			@Override
//			protected Object computeValue(Class type) {
//				return createBasicProxy( type );
//			}
//		};
//
//		public Object createClassValueCachedBasicProxy(final Class<?> beanClass) {
//			return proxyCache.get( beanClass );
//		}

		public Object createBasicProxy(final Class<?> beanClass) {
			final BasicProxyFactory basicProxyFactory = bytecodeProvider.getProxyFactoryFactory().buildBasicProxyFactory( beanClass );
			final Object instantiatedProxy = basicProxyFactory.getProxy();
			//A couple of essential assertions
			Assert.assertTrue( instantiatedProxy instanceof ProxyConfiguration );
			Assert.assertTrue( beanClass.isAssignableFrom( instantiatedProxy.getClass() ) );
			return instantiatedProxy;
		}

		public void transitionToStarted() {
			sessionFactoryLifecycle.sessionFactoryCreated( null );
		}

		public void transitionToStopped() {
			sessionFactoryLifecycle.sessionFactoryClosing( null );
			sessionFactoryLifecycle.sessionFactoryClosed( null );
		}
	}


}
