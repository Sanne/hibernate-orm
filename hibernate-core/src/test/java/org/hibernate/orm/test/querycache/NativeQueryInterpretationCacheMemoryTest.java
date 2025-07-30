/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.querycache;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.cfg.AvailableSettings;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.spi.QueryInterpretationCache;
import org.hibernate.query.internal.QueryInterpretationCacheStandardImpl;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.ServiceRegistry;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.hibernate.testing.orm.junit.Setting;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test to verify native query interpretation cache memory behavior
 */
@DomainModel(
		annotatedClasses = {
				Book.class
		}
)
@ServiceRegistry(
		//Making the size a fair bit larger so to help with estimates accuracy:
		settings = {
				@Setting(name = AvailableSettings.QUERY_PLAN_CACHE_MAX_SIZE, value = "65536")
		}
)
@SessionFactory
public class NativeQueryInterpretationCacheMemoryTest {

	public static final int TEST_CACHE_SIZE = 65536;//Needs to match the @Setting above

	@Test
	public void testNativeQueryParameterCacheMemoryUsage(SessionFactoryScope scope) {
		List<String> nativeQueries = new ArrayList<>();

		// Generate queries with various parameter patterns to stress the cache
		for ( int i = 0; i <= (TEST_CACHE_SIZE / 3); i++ ) {
			nativeQueries.add("SELECT * FROM Book WHERE title = :title" + i);
			nativeQueries.add("SELECT * FROM Book WHERE isbn = :isbn" + i + " AND price > :minPrice" + i);
			nativeQueries.add("SELECT COUNT(*) FROM Book WHERE author = :author" + i + " AND year = :year" + i + " AND category = :category" + i);
		}

		QueryInterpretationCache interpretationCache = scope.getSessionFactory()
				.getQueryEngine()
				.getInterpretationCache();

		//Check the cache is on:
		assertNotNull(interpretationCache);
		assertTrue(interpretationCache instanceof QueryInterpretationCacheStandardImpl);

		final long beforeRun = memoryConsumptionEstimateBytes();

		// Execute all native queries to populate the parameter interpretation cache
		scope.inTransaction(session -> {
			for (String queryString : nativeQueries) {
				NativeQuery<?> query = session.createNativeQuery(queryString);
				// Just create the query to trigger parameter interpretation caching
				// Don't execute to avoid database dependencies
				assertNotNull(query.getParameterMetadata());
			}
		});

		System.out.println("Native query parameter cache populated with " + nativeQueries.size() + " queries");
		final long afterRun = memoryConsumptionEstimateBytes();

		System.out.println("Increase in memory per cached entry, considering ~2048 elements cache: " + (afterRun - beforeRun)/TEST_CACHE_SIZE + " bytes");
	}

	private static long memoryConsumptionEstimateBytes() {
		// Unfortunately often it's just a hint, but better than nothing:
		System.gc();

		// Try to get memory usage information
		Runtime runtime = Runtime.getRuntime();
		return runtime.totalMemory() - runtime.freeMemory();
	}
}
