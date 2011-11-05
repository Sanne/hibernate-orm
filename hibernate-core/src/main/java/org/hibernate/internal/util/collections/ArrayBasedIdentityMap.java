/* 
 * Hibernate, Relational Persistence for Idiomatic Java
 * 
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.hibernate.internal.util.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * This implementation is not threadsafe, and meant to contain small collections.
 *  
 * @author Sanne Grinovero <sanne@hibernate.org> (C) 2011 Red Hat Inc.
 */
public class ArrayBasedIdentityMap<K, V> implements Map<K, V>, java.io.Serializable, Cloneable {

	private int size;
	private Object[] keys;
	private Object[] values;

	ArrayBasedIdentityMap() {
		this( 30 );
	}

	public ArrayBasedIdentityMap(int maxSize) {
		if ( maxSize <= 0) {
			throw new IllegalArgumentException( "Maximum size of collection should be > 0" );
		}
		keys = new Object[maxSize];
		values = new Object[maxSize];
	}

	public ArrayBasedIdentityMap(ArrayBasedIdentityMap other, int newMaxSize) {
		if ( newMaxSize < other.size ) {
			throw new IllegalArgumentException( "Can't fit all elements in the new specified MaxSize" );
		}
		keys = Arrays.copyOf( other.keys, newMaxSize );
		values = Arrays.copyOf( other.values, newMaxSize );
		size = other.size;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsKey(Object key) {
		checkNotNull( key );
		for ( int i = 0; i < size; i++ ) {
			if ( keys[i] == key )
				return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		checkNotNull( value );
		for ( int i = 0; i < size; i++ ) {
			if ( values[i] == value )
				return true;
		}
		return false;
	}

	@Override
	public V get(Object key) {
		checkNotNull( key );
		for ( int i = 0; i < size; i++ ) {
			if ( keys[i] == key )
				return (V) values[i];
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		checkNotNull( key );
		checkNotNull( value );
		//try finding an easy spot first:
		for ( int i = size; i < keys.length; i++ ) {
			if ( keys[i] == null )
				return (V) putAt(key, value, i);
		}
		//try filling a gap left from remove operations:
		for ( int i = 0; i < size; i++ ) {
			if ( keys[i] == null )
				return (V) putAt(key, value, i);
		}
		// no space left! degenerate to a real IdentityMap
		return null;
	}

	/**
	 * @param key
	 * @param value
	 * @param index
	 * @return
	 */
	private V putAt(K key, V value, int index) {
		V previousValue = (V) values[index];
		values[index] = value;
		keys[index] = key;
		size++;
		return previousValue;
	}

	@Override
	public V remove(Object key) {
		checkNotNull( key );
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		size = 0;
		Arrays.fill( keys, null ); //maybe we should skip these?
		Arrays.fill( values, null );
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		return (Collection<V>) Arrays.asList( values );
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	private class MyValues implements Collection<V> {

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return ArrayBasedIdentityMap.this.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return ArrayBasedIdentityMap.this.containsValue( o );
		}

		@Override
		public Iterator<V> iterator() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object[] toArray() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public <T> T[] toArray(T[] a) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean add(V e) {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}

		@Override
		public boolean remove(Object o) {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException( "this is a readonly collection: modifications not allowed" );
		}
	}

	@Override
	public ArrayBasedIdentityMap<K, V> clone() {
		// To have the clone use the same maximum write size use our current array size
		return new ArrayBasedIdentityMap<K, V>( this, this.keys.length );
	}

	private static void checkNotNull(Object key) {
		if ( key == null ) {
			throw new IllegalArgumentException( "This Map implementation does not support null keys or values" );
		}
	}

}
