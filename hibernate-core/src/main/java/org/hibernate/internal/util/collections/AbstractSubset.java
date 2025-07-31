/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.collections;

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

/**
 * Abstract base class for subset implementations that provides all read-only
 * operations using efficient bit manipulation techniques.
 *
 * @param <V> the type of elements in this subset
 * @author Sanne Grinovero
 * @since 7.1
 */
public abstract class AbstractSubset<V> implements Set<V> {

	protected final UniverseForSubsets<V> universe;
	protected final BitSet bitSet;

	protected AbstractSubset(UniverseForSubsets<V> universe, BitSet bitSet) {
		this.universe = Objects.requireNonNull(universe);
		this.bitSet = Objects.requireNonNull(bitSet);
	}

	@Override
	public int size() {
		return bitSet.cardinality();
	}

	@Override
	public boolean isEmpty() {
		return bitSet.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		Objects.requireNonNull(o);
		final int idx = universe.indexOf(o);
		if (idx < 0) {
			return false;
		}
		return bitSet.get(idx);
	}

	@Override
	public Iterator<V> iterator() {
		// Thread safety: This iterator is not thread-safe and must not be used
		// concurrently with modifications to this subset. External synchronization
		// is required if the subset may be modified during iteration.
		//
		// Design assumption: the universe might exceptionally expand during
		// an iteration, but the state represented by this subset would not
		// be affected so the iterator isn't impacted.
		// Universe expansion might happen concurrently with iteration.
		return new Iterator<V>() {
			private int currentBitIndex = bitSet.nextSetBit(0);

			@Override
			public boolean hasNext() {
				return currentBitIndex >= 0;
			}

			@Override
			public V next() {
				if (currentBitIndex < 0) {
					throw new NoSuchElementException();
				}

				V value = universe.orderedValues.get(currentBitIndex);
				currentBitIndex = bitSet.nextSetBit(currentBitIndex + 1);
				return value;
			}

			// Note: remove() operation is not supported - inherited implementation
			// throws UnsupportedOperationException
		};
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		// Optimized path for subsets from the same universe
		if (collection instanceof AbstractSubset<?> otherSubset) {
			if (otherSubset.universe == this.universe) {
				BitSet temp = (BitSet) otherSubset.bitSet.clone();
				temp.andNot(this.bitSet);
				return temp.isEmpty();
			}
		}

		// Fall back to element-by-element check
		for (Object o : collection) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		return bitSet.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Set<?> other)) {
			return false;
		}
		if (size() != other.size()) {
			return false;
		}

		// Optimized comparison with other subsets from same universe
		if (obj instanceof AbstractSubset<?> otherSubset && otherSubset.universe == this.universe) {
			return bitSet.equals(otherSubset.bitSet);
		}

		// Fall back to standard Set equals contract
		return containsAll(other);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		boolean first = true;
		for (V element : this) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(element);
			first = false;
		}
		sb.append(']');
		return sb.toString();
	}

	// toArray operations still not supported for memory efficiency
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException("toArray() is not supported for memory efficiency reasons");
	}

	@Override
	public <T> T[] toArray(T[] array) {
		throw new UnsupportedOperationException("toArray() is not supported for memory efficiency reasons");
	}
}