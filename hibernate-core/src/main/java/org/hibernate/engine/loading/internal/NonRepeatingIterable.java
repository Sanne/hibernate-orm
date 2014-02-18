/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) 2014, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.engine.loading.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;


/**
 * This structure is similar to a LinkedHashSet: a de-duplicating structure
 * which preserves iteration order.
 * Compared to the LinkedHashSet, this one is very simple and does tradeoff
 * random access for a lower memory consumption.
 *
 * Warning: not suited for large collections! Both addition and Removal are O(n).
 *
 * @author Sanne Grinovero
 * @since 4.3
 */
public final class NonRepeatingIterable<T> implements Iterable<T> {

	private Entry listRoot;

	/**
	 * Create a new NonRepeatingIterable.
	 */
	public NonRepeatingIterable() {
	}

	/**
	 * The value is added to the list if it's not-null and if it's not
	 * in the list already.
	 * Otherwise does nothing.
	 *
	 * @param value The value to add to the internal list
	 */
	public void add(final T value) {
		if ( value == null ) {
			return;
		}
		else if ( listRoot == null ) {
			listRoot = new Entry( value );
		}
		else {
			Entry current = listRoot;
			while ( ! value.equals( current.value) ) {
				if ( current.next != null) {
					current = current.next;
				}
				else {
					current.next = new Entry( value );
					return;
				}
			}
			//on exit condition (when the key already exists)
			//we don't have anything left to do.
		}
	}

	/**
	 * Remove the value, if it's not null and if it exists.
	 * Otherwise does nothing.
	 * 
	 * @param value
	 */
	public void remove(final T value) {
		if ( value == null || listRoot == null ) {
			return;
		}
		else {
			Entry current = listRoot;
			Entry previous = null;
			while ( ! value.equals( current.value) ) {
				if ( current.next != null) {
					previous = current;
					current = current.next;
				}
				else {
					//nothing matching, so nothing to do
					return;
				}
			}
			if ( previous == null ) {
				//discard the head
				listRoot = listRoot.next;
			}
			else {
				previous.next = current.next;
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		if ( listRoot != null ) {
			return new EntryIterator( listRoot );
		}
		else {
			return Collections.EMPTY_LIST.iterator();
		}
	}

	private static class Entry<T> {
		private final T value;
		private Entry<T> next;
		public Entry(T value) {
			this.value = value;
		}
	}

	private static final class EntryIterator<T> implements Iterator {

		private Entry<T> next;

		public EntryIterator(Entry listRoot) {
			next = listRoot;
		}

		@Override
		public boolean hasNext() {
			return next != null;
		}

		@Override
		public Object next() {
			if ( next == null ) {
				throw new NoSuchElementException();
			}
			Object val = next.value;
			next = next.next;
			return val;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
