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
 * This container is similar in function to a Set, but optimises
 * for memory consumption when we need to frequently represent
 * combinations of elements from a closed set.
 * For example, the set of table names for a certain operation:
 * this will frequently be a small set of names, and those names will
 * always be part of the same universe of all table names of this particular
 * database.
 * Another example is to represent some entity names, out of all known
 * entities.
 * Usage: the class {@link UniverseForSubsets} should be created once to represent
 * the universe of all possible elements; from there invoke {@link UniverseForSubsets#createSubset()}
 * to create an empty set from this universe; on such empty set operate normally.
 * This will ensure that the UniverseForSubsets does the heavy lifting in terms
 * of memory retention: the various subsets are represented as a bitmask on the
 * elements of the universe, making the representation of multiple subsets
 * far cheaper in terms of memory, as long as you make sure to have them share
 * the universe instance.
 * Several other set operations on it also happen to be more efficient as a welcome
 * side effect.
 * It's implementing {@link Set} as this simplifies introduction in the project, but
 * it's tempting to drop this interface in favour for more specialized methods.
 * @param <V>
 */
public final class Subset<V> implements Set<V> {

	private final UniverseForSubsets<V> universe;
	private final BitSet bitSet;

	Subset(UniverseForSubsets<V> universe) {
		this.universe = universe;
		//Design assumption: while we support the growth of the universe,
		//this is done to handle exceptional situations.
		//We're aware that dynamic resizing of the BitSet is not efficient;
		//this is acceptable as we expect the typical usage to not trigger this
		//frequently.
		this.bitSet = new BitSet( universe.size() );
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
		Objects.requireNonNull( o );
		final int idx = universe.indexOf( o );
		if ( idx < 0 ) {
			//o is unknown in this universe
			return false;
		}
		else {
			return bitSet.get( idx );
		}
	}

	@Override
	public Iterator<V> iterator() {
		//Design assumption: the universe might exceptionally expand concurrently
		//during an iteration, but the state represented by this subset would not
		//be affected to the iterator isn't impacted.
		//For this reason, the process of expansion in the universe needs to be careful
		//to not repurpose existing index positions.
		//(This is trivial as we don't support shrinking of the universe)
		return new Iterator<V>() {
			private int currentBitIndex = bitSet.nextSetBit( 0 );

			@Override
			public boolean hasNext() {
				return currentBitIndex >= 0;
			}

			@Override
			public V next() {
				if ( currentBitIndex < 0 ) {
					throw new NoSuchElementException();
				}

				V value = universe.orderedValues.get( currentBitIndex );
				currentBitIndex = bitSet.nextSetBit( currentBitIndex + 1 );
				return value;
			}
		};
	}

	@Override
	public Object[] toArray() {
		//Could be implemented, but question the use case being appropriate
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T[] toArray(T[] ts) {
		//Could be implemented, but question the use case being appropriate
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(V o) {
		Objects.requireNonNull( o );
		int idx = universe.ensureExists( o );
		//The following need of tracking modifications makes me question the choice of implementing java.util.Set:
		//we might be better off by implementing a reduced contract which would offer `void add(V)`.
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
			//The following need of tracking modifications makes me question the choice of implementing java.util.Set:
			//we might be better off by implementing a reduced contract which would offer `void remove(V)`.
			boolean wasSet = this.bitSet.get( i );
			if ( wasSet ) {
				this.bitSet.clear( i );
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		if ( collection instanceof Subset<?> subset ) {
			if ( subset.universe == this.universe ) {
				//Both subsets share the same universe, can optimize by comparing bitsets
				BitSet temp = (BitSet) subset.bitSet.clone();
				temp.andNot( this.bitSet );
				return temp.isEmpty();
			}
		}
		for ( Object o : collection ) {
			if ( !contains( o ) ) {
				return false;
			}
		}
		return true;
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
		//Could be implemented, but question the use case being appropriate
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		//Could be implemented, but question the use case being appropriate
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		this.bitSet.clear();
	}

	public Subset<V> unmodifiableCopy() {
		return this; //TODO
	}
}