package org.hibernate.test.cache;

import java.lang.reflect.Field;

import org.hibernate.cache.internal.CollectionCacheInvalidator;

final class CollectionCacheTestingHelper {

	// Implementor's note: not agreeing with this approach, but refactoring existing tests
	static void enableExceptionPropagation(boolean b) {
		final Field propagate_exception;
		try {
			propagate_exception = CollectionCacheInvalidator.class.getField( "PROPAGATE_EXCEPTION" );
			propagate_exception.setAccessible( true );
			propagate_exception.set( null, b );
		}
		catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException( e );
		}
	}

}
