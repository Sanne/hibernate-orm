/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.collections;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe registry that maintains a universe of values and assigns unique ordinal
 * positions to each value. This class serves as a foundation for creating efficient
 * {@link Subset} instances that can represent collections of values using bit manipulation
 * techniques based on their ordinal positions.
 * 
 * <h2>Purpose and Design</h2>
 * <p>
 * The universe maintains both a fast lookup map for checking existence and retrieving
 * ordinal positions, as well as an ordered list that preserves insertion order and
 * allows for efficient subset operations. Values are assigned consecutive ordinal
 * positions starting from zero, based on their insertion order.
 * 
 * <h2>Thread Safety</h2>
 * <p>
 * This implementation is thread-safe for concurrent read and write operations. Write
 * operations (adding new values) are synchronized using double-checked locking, while
 * read operations can proceed concurrently without blocking through the use of volatile
 * references and copy-on-write semantics for the ordered values list.
 * 
 * <h2>Usage Pattern</h2>
 * <p>
 * Write (add) operations are expected to happen primarily during bootstrap phase.
 * While the ability to add elements later provides flexibility, it should be considered
 * an exceptional circumstance as it may impact performance of concurrent operations.
 *
 * @param <V> the type of values stored in this universe. Values must implement
 *            {@link Object#equals(Object)} and {@link Object#hashCode()} correctly
 *            for proper functioning of the internal map.
 * 
 * @author Sanne Grinovero
 * @see Subset
 * @since 7.1
 */
public final class UniverseForSubsets<V> {

	private final ConcurrentMap<V, Integer> map = new ConcurrentHashMap<>();
	volatile ArrayList<V> orderedValues = new ArrayList<>();

	/**
	 * Creates a new empty universe for subsets.
	 */
	public UniverseForSubsets() {
	}

	/**
	 * Returns the current size of this universe (the number of distinct values
	 * currently registered).
	 *
	 * @return the number of values currently in this universe
	 */
	int size() {
		assert orderedValues.size() == map.size();
		return map.size();
	}

	/**
	 * Returns the ordinal position of the specified object in this universe.
	 * <p>
	 * The ordinal position is a zero-based index that represents the order
	 * in which values were added to this universe. This position is used
	 * by {@link Subset} instances for efficient bit-based operations.
	 *
	 * @param o the object whose ordinal position is to be returned
	 * @return the zero-based ordinal position of the object, or {@code -1}
	 * if the object is not present in this universe
	 * @throws NullPointerException if the specified object is null
	 */
	int indexOf(final Object o) {
		Integer i = map.get( o );
		if ( i != null ) {
			return i;
		}
		else {
			return -1;
		}
	}

	/**
	 * Returns the ordinal position for the specified value, adding it to this
	 * universe if it doesn't already exist.
	 * <p>
	 * This method guarantees that the specified value will have an ordinal
	 * position in this universe after the method returns. If the value is
	 * already present, its existing ordinal position is returned. If the
	 * value is not present, it is added to the universe and assigned the
	 * next available ordinal position.
	 * <p>
	 * This operation is thread-safe and atomic - concurrent calls with the
	 * same value will return the same ordinal position.
	 *
	 * @param o the value to ensure exists in this universe
	 * @return the zero-based ordinal position of the value
	 * @throws NullPointerException if the specified value is null
	 */
	int ensureExists(V o) {
		Integer i = map.get( o );
		if ( i != null ) {
			return i;
		}
		else {
			return lockAdd( o );
		}
	}

	/**
	 * Thread-safe method to add a new value to this universe.
	 * <p>
	 * This method uses double-checked locking to ensure thread safety while
	 * minimizing synchronization overhead. It creates a new copy of the
	 * ordered values list to maintain thread-safe reads via the volatile
	 * reference.
	 *
	 * @param o the value to add to the universe
	 * @return the ordinal position assigned to the value
	 */
	private synchronized int lockAdd(V o) {
		Integer i = map.get( o );
		if ( i != null ) {
			return i;
		}
		else {
			ArrayList<V> current = orderedValues;
			ArrayList<V> newList = new ArrayList<>( current );  // Create a new list
			int idx = newList.size();
			newList.add( o );
			orderedValues = newList;  // Publish the new reference
			map.put( o, idx );
			return idx;

		}
	}

	/**
	 * Creates a new empty subset that can efficiently store elements from this universe.
	 * <p>
	 * The returned subset uses this universe to determine ordinal positions for
	 * its elements, enabling efficient set operations through bit manipulation.
	 *
	 * @return a new empty subset associated with this universe
	 * @see Subset
	 */
	public Subset<V> createSubset() {
		return new Subset<>( this );
	}
}