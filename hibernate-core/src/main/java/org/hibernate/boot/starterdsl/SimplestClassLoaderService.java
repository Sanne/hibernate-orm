/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.boot.starterdsl;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;

import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

public final class SimplestClassLoaderService implements ClassLoaderService {

	private static final CoreMessageLogger log = CoreLogging.messageLogger( SimplestClassLoaderService.class );
	public static final ClassLoaderService INSTANCE = new SimplestClassLoaderService();

	private SimplestClassLoaderService() {
		// use #INSTANCE when you need one
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Class<T> classForName(String className) {
		try {
			return (Class<T>) Class.forName( className, false, getClassLoader() );
		}
		catch (Exception | LinkageError e) {
			throw new ClassLoadingException( "Unable to load class [" + className + "]", e );
		}
	}

	@Override
	public URL locateResource(String name) {
		URL resource = getClassLoader().getResource( name );
		if ( resource == null ) {
			log.debugf(
					"Loading of resource '%s' failed.",
					name
			);
		}
		else {
			log.tracef( "Successfully loaded resource '%s'", name );
		}
		return resource;
	}

	@Override
	public InputStream locateResourceStream(String name) {
		InputStream resourceAsStream = getClassLoader().getResourceAsStream( name );
		if ( resourceAsStream == null ) {
			log.debugf(
					"Loading of resource '%s' failed.",
					name
			);
		}
		else {
			log.tracef( "Successfully loaded resource '%s'", name );
		}
		return resourceAsStream;
	}

	@Override
	public List<URL> locateResources(String name) {
		log.debugf(
				"locateResources (plural form) was invoked for resource '%s'. Is there a real need for this plural form?",
				name
		);
		try {
			Enumeration<URL> resources = getClassLoader().getResources( name );
			List<URL> resource = new ArrayList<>();
			while ( resources.hasMoreElements() ) {
				resource.add( resources.nextElement() );
			}
			return resource;
		}
		catch (IOException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public <S> Collection<S> loadJavaServices(Class<S> serviceContract) {
		ServiceLoader<S> serviceLoader = ServiceLoader.load( serviceContract, getClassLoader() );
		final LinkedHashSet<S> services = new LinkedHashSet<S>();
		for ( S service : serviceLoader ) {
			services.add( service );
		}
		return services;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <T> T generateProxy(InvocationHandler handler, Class... interfaces) {
		return (T) Proxy.newProxyInstance(
				getClassLoader(),
				interfaces,
				handler
		);
	}

	@Override
	public <T> T workWithClassLoader(Work<T> work) {
		ClassLoader classLoader = getClassLoader();
		return work.doWork( classLoader );
	}

	@Override
	public void stop() {
		// easy!
	}

	private ClassLoader getClassLoader() {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if ( cl == null ) {
			return SimplestClassLoaderService.class.getClassLoader();
		}
		return cl;
	}

}
