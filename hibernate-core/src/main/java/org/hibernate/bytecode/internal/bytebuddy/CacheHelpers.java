package org.hibernate.bytecode.internal.bytebuddy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.AssertionFailure;

import net.bytebuddy.TypeCache;

public class CacheHelpers {

	private CacheHelpers() {
		//do not instantiate
	}

	public static TypeCache.SimpleKey getCacheKey(Class<?> superClass, Class<?>[] interfaces) {
		if ( interfaces != null && interfaces.length > 0 ) {
			final Set<Class<?>> key = new HashSet<>( interfaces.length );
			for ( Class<?> c : interfaces ) {
				key.add( c );
			}
			if ( superClass != null ) {
				key.add( superClass );
			}
			return new TypeCache.SimpleKey( key );
		}
		else {
			if ( superClass == null ) {
				throw new AssertionFailure( "attempting to build proxy without any superclass or interfaces" );
			}
			return new TypeCache.SimpleKey( Collections.singleton( superClass ) );
		}
	}
}
