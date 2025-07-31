/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.collections;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class UniverseForSubsets<V> {

	private final ConcurrentMap<V,Integer> map = new ConcurrentHashMap<>();
	volatile ArrayList<V> orderedValues = new ArrayList<>();

	public UniverseForSubsets() {
	}

	int size() {
		assert orderedValues.size() == map.size();
		return map.size();
	}

	/**
	 * @param o
	 * @return the ordinal position of the given Object o, or -1 if this particular object is not known in this universe.
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
	 * @param o
	 * @return the positional index for the given Object,
	 * potentially after implicitly having added the object
	 * to this universe.
	 */
	int ensureExists(V o) {
		Integer i = map.get( o );
		if ( i != null ) {
			return i;
		}
		else {
			return lockAdd(o);
		}
	}

	private synchronized int lockAdd(V o) {
		Integer i = map.get( o );
		if ( i != null ) {
			return i;
		}
		else {
			ArrayList<V> current = orderedValues;
			ArrayList<V> newList = new ArrayList<>(current);  // Create a new list
			int idx = newList.size();
			newList.add( o );
			orderedValues = newList;  // Publish the new reference
			map.put( o, idx );
			return idx;

		}
	}

	public Subset<V> createSubset() {
		return new Subset<>( this );
	}
}
