package org.hibernate.bytecode.internal.bytebuddy;

import java.util.Set;

import org.hibernate.LazyInitializationException;
import org.hibernate.engine.spi.PrimeAmongSecondarySupertypes;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.ProxyFactory;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyProxyFactory;
import org.hibernate.proxy.pojo.bytebuddy.ByteBuddyProxyHelper;

import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;

public class SuperTypesEnhancementTest {

	private static final ByteBuddyProxyHelper helper = new ByteBuddyProxyHelper( new ByteBuddyState() );

	@Test
	@TestForIssue( jiraKey = "TBD" )
	public void test() {
		ProxyFactory enhancer = createProxyFactory( SampleClass.class, HibernateProxy.class );
		final Object proxy = enhancer.getProxy( Integer.valueOf( 1 ), null );
		boolean instanceofWorks = proxy instanceof HibernateProxy;
		Assert.assertTrue( instanceofWorks );
		PrimeAmongSecondarySupertypes casted = (PrimeAmongSecondarySupertypes) proxy;
		final HibernateProxy extracted = casted.asHibernateProxy();
		Assert.assertNotNull( extracted );
		Assert.assertSame( proxy, extracted );
		testForLIE( (SampleClass) proxy );
	}

	/**
	 * Self-check: verify that this is in fact a lazy proxy
	 */
	private void testForLIE(SampleClass sampleProxy) {
		SampleClass other = new SampleClass();
		Assert.assertEquals( 7, other.additionMethod( 3,4 ) );
		Assert.assertThrows( LazyInitializationException.class, () -> sampleProxy.additionMethod( 3, 4 ) );
	}

	private ProxyFactory createProxyFactory(Class<?> persistentClass, Class<?>... interfaces) {
		ByteBuddyProxyFactory proxyFactory = new ByteBuddyProxyFactory( helper );
		proxyFactory.postInstantiate( "", persistentClass, Set.of( interfaces ), null, null, null );
		return proxyFactory;
	}

	//Just a class with some fields and methods to proxy
	static class SampleClass {
		int intField;
		String stringField;

		public int additionMethod(int a, int b) {
			return a + b;
		}
	}

}
