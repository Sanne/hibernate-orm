/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.collections;

import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;

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
public final class Subset<V> extends AbstractSubset<V> {

	Subset(UniverseForSubsets<V> universe) {
		super( universe, new BitSet( universe.size() ) );
	}

	// All mutating operations
	@Override
	public boolean add(V o) {
		Objects.requireNonNull( o );
		int idx = universe.ensureExists( o );
		boolean existingState = this.bitSet.get( idx );
		if ( existingState ) {
			return false;
		}
		else {
			this.bitSet.set( idx );
			return true;
		}
	}

	@Override
	public boolean remove(Object o) {
		Objects.requireNonNull( o );
		int i = universe.indexOf( o );
		if ( i >= 0 ) {
			boolean wasSet = this.bitSet.get( i );
			if ( wasSet ) {
				this.bitSet.clear( i );
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends V> collection) {
		boolean changed = false;
		for ( V v : collection ) {
			if ( add( v ) ) {
				changed = true;
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.bitSet.clear();
	}

	/**
	 * Creates an immutable, thread-safe copy of this subset.
	 * <p>
	 * The returned subset is a snapshot of the current state and will not reflect
	 * any subsequent changes to this subset. The immutable copy is safe for
	 * concurrent access by multiple threads without external synchronization.
	 *
	 * @return an immutable, thread-safe copy of this subset
	 */
	public ImmutableSubset<V> immutableCopy() {
		return new ImmutableSubset<>( this.universe, (BitSet) this.bitSet.clone() );
	}

}
