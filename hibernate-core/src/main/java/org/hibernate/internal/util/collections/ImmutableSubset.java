/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.collections;

import java.util.BitSet;
import java.util.Collection;

/**
 * An immutable, thread-safe implementation of a subset that shares the same
 * universe and memory-efficient characteristics as {@link Subset}, but provides
 * only read-only operations with performance optimizations through caching.
 *
 * <h2>Performance Optimizations</h2>
 * <p>
 * This class caches expensive computations like {@link #size()} and {@link #hashCode()}
 * since the immutable nature guarantees these values will never change. This makes
 * repeated calls to these methods extremely fast.
 *
 * <h2>Thread Safety</h2>
 * <p>
 * This class is <strong>thread-safe</strong> and can be safely accessed
 * concurrently by multiple threads without external synchronization. The
 * immutability is enforced by cloning the BitSet during construction and
 * throwing exceptions for all mutation operations.
 *
 * @param <V> the type of elements in this subset
 * @author Sanne Grinovero
 * @see Subset
 * @see UniverseForSubsets
 * @since 7.1
 */
public final class ImmutableSubset<V> extends AbstractSubset<V> {

	// Cached expensive computations - these never change due to immutability
	private final int cachedSize;
	private final int cachedHashCode;

	/**
	 * Package-private constructor for creating immutable subsets.
	 * Only {@link Subset} should create instances of this class.
	 */
	ImmutableSubset(UniverseForSubsets<V> universe, BitSet bitSet) {
		super(universe, bitSet);
		// Pre-compute and cache expensive operations
		this.cachedSize = bitSet.cardinality();
		this.cachedHashCode = bitSet.hashCode();
	}

	// Optimized read operations using cached values
	@Override
	public int size() {
		return cachedSize;
	}

	@Override
	public boolean isEmpty() {
		return cachedSize == 0;
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	// All mutating operations throw UnsupportedOperationException
	@Override
	public boolean add(V v) {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}

	@Override
	public boolean addAll(Collection<? extends V> collection) {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException("ImmutableSubset does not support modification operations");
	}
}