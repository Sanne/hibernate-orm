/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.cache;

import java.util.function.Function;

/**
 * Contract for internal caches.
 * We call it "internal" Cache to disambiguate from the canonical entity 2LC, as that one is more commonly
 * used by users and therefore of public knowledge.
 * We highly prefer caches to be implemented by reputable caching libraries, this reduces our maintenance complexity
 * (as maintaining a proper cache implementation is not at all trivial) and allows for people to experiment with
 * various algorithms, including state-of-the-art that we might not be familiar with.
 * For these reasons, we rely on this internal interface and encourage plugging in an external implementation;
 * at the time of writing this we'll have a legacy implementation for backwards compatibility reasons but the general
 * idea is to deprecate it and eventually require a third party implementation.
 */
public interface InternalCache<K, V> {

	/**
	 * @return An estimate of the number of values contained in the cache.
	 */
	int heldElementsEstimate();

	V get(K key);

	void put(K key, V value);

	void clear();

	V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);
}
