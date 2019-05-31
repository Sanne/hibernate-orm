/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.internal.util.collections;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.internal.build.AllowSysOut;
import org.hibernate.type.Type;

public final class ArrayHelper {

	public static boolean contains(Object[] array, Object object) {
		return indexOf( array, object ) > -1;
	}

	public static int indexOf(Object[] array, Object object) {
		for ( int i = 0; i < array.length; i++ ) {
			if ( array[i].equals( object ) ) {
				return i;
			}
		}
		return -1;
	}

	public static <T> T[] filledArray(T value, Class<T> valueJavaType, int size) {
		final T[] array = (T[]) Array.newInstance( valueJavaType, size );
		Arrays.fill( array, value );
		return array;
	}

	public static String[] toStringArray(Object[] objects) {
		int length = objects.length;
		String[] result = new String[length];
		for ( int i = 0; i < length; i++ ) {
			result[i] = objects[i].toString();
		}
		return result;
	}

	public static String[] fillArray(String value, int length) {
		String[] result = new String[length];
		Arrays.fill( result, value );
		return result;
	}

	public static int[] fillArray(int value, int length) {
		int[] result = new int[length];
		Arrays.fill( result, value );
		return result;
	}

	public static LockMode[] fillArray(LockMode lockMode, int length) {
		LockMode[] array = new LockMode[length];
		Arrays.fill( array, lockMode );
		return array;
	}

	public static LockOptions[] fillArray(LockOptions lockOptions, int length) {
		LockOptions[] array = new LockOptions[length];
		Arrays.fill( array, lockOptions );
		return array;
	}

	public static String[] toStringArray(Collection coll) {
		return (String[]) coll.toArray( new String[coll.size()] );
	}

	public static String[][] to2DStringArray(Collection coll) {
		return (String[][]) coll.toArray( new String[coll.size()][] );
	}

	public static int[][] to2DIntArray(Collection coll) {
		return (int[][]) coll.toArray( new int[coll.size()][] );
	}

	public static Type[] toTypeArray(Collection coll) {
		return (Type[]) coll.toArray( new Type[coll.size()] );
	}

	public static int[] toIntArray(Collection coll) {
		Iterator iter = coll.iterator();
		int[] arr = new int[coll.size()];
		int i = 0;
		while ( iter.hasNext() ) {
			arr[i++] = (Integer) iter.next();
		}
		return arr;
	}

	public static boolean[] toBooleanArray(Collection coll) {
		Iterator iter = coll.iterator();
		boolean[] arr = new boolean[coll.size()];
		int i = 0;
		while ( iter.hasNext() ) {
			arr[i++] = (Boolean) iter.next();
		}
		return arr;
	}

	public static Object[] typecast(Object[] array, Object[] to) {
		return java.util.Arrays.asList( array ).toArray( to );
	}

	//Arrays.asList doesn't do primitive arrays
	public static List toList(Object array) {
		if ( array instanceof Object[] ) {
			return Arrays.asList( (Object[]) array ); //faster?
		}
		int size = Array.getLength( array );
		ArrayList list = new ArrayList( size );
		for ( int i = 0; i < size; i++ ) {
			list.add( Array.get( array, i ) );
		}
		return list;
	}

	public static String[] slice(String[] strings, int begin, int length) {
		String[] result = new String[length];
		System.arraycopy( strings, begin, result, 0, length );
		return result;
	}

	public static Object[] slice(Object[] objects, int begin, int length) {
		Object[] result = new Object[length];
		System.arraycopy( objects, begin, result, 0, length );
		return result;
	}

	public static List toList(Iterator iter) {
		List list = new ArrayList();
		while ( iter.hasNext() ) {
			list.add( iter.next() );
		}
		return list;
	}

	public static String[] join(String[] x, String[] y) {
		String[] result = new String[x.length + y.length];
		System.arraycopy( x, 0, result, 0, x.length );
		System.arraycopy( y, 0, result, x.length, y.length );
		return result;
	}

	public static String[] join(String[] x, String[] y, boolean[] use) {
		String[] result = new String[x.length + countTrue( use )];
		System.arraycopy( x, 0, result, 0, x.length );
		int k = x.length;
		for ( int i = 0; i < y.length; i++ ) {
			if ( use[i] ) {
				result[k++] = y[i];
			}
		}
		return result;
	}

	public static int[] join(int[] x, int[] y) {
		int[] result = new int[x.length + y.length];
		System.arraycopy( x, 0, result, 0, x.length );
		System.arraycopy( y, 0, result, x.length, y.length );
		return result;
	}

	@SuppressWarnings({"unchecked"})
	public static <T> T[] join(T[] x, T... y) {
		T[] result = (T[]) Array.newInstance( x.getClass().getComponentType(), x.length + y.length );
		System.arraycopy( x, 0, result, 0, x.length );
		System.arraycopy( y, 0, result, x.length, y.length );
		return result;
	}

	public static final boolean[] TRUE = {true};
	public static final boolean[] FALSE = {false};

	private ArrayHelper() {
	}

	public static String toString(Object[] array) {
		StringBuilder sb = new StringBuilder();
		sb.append( "[" );
		for ( int i = 0; i < array.length; i++ ) {
			sb.append( array[i] );
			if ( i < array.length - 1 ) {
				sb.append( "," );
			}
		}
		sb.append( "]" );
		return sb.toString();
	}

	public static boolean isAllNegative(int[] array) {
		for ( int anArray : array ) {
			if ( anArray >= 0 ) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAllTrue(boolean... array) {
		for ( boolean anArray : array ) {
			if ( !anArray ) {
				return false;
			}
		}
		return true;
	}

	public static int countTrue(boolean... array) {
		int result = 0;
		for ( boolean anArray : array ) {
			if ( anArray ) {
				result++;
			}
		}
		return result;
	}

	public static boolean isAllFalse(boolean... array) {
		for ( boolean anArray : array ) {
			if ( anArray ) {
				return false;
			}
		}
		return true;
	}

	public static <T> void addAll(Collection<T> collection, T[] array) {
		collection.addAll( Arrays.asList( array ) );
	}

	public static final String[] EMPTY_STRING_ARRAY = {};
	public static final int[] EMPTY_INT_ARRAY = {};
	public static final boolean[] EMPTY_BOOLEAN_ARRAY = {};
	public static final Class[] EMPTY_CLASS_ARRAY = {};
	public static final Object[] EMPTY_OBJECT_ARRAY = {};
	public static final Type[] EMPTY_TYPE_ARRAY = {};
	public static final byte[] EMPTY_BYTE_ARRAY = {};

	public static int[] getBatchSizes(int maxBatchSize) {
		int batchSize = maxBatchSize;
		int n = 1;
		while ( batchSize > 1 ) {
			batchSize = getNextBatchSize( batchSize );
			n++;
		}
		int[] result = new int[n];
		batchSize = maxBatchSize;
		for ( int i = 0; i < n; i++ ) {
			result[i] = batchSize;
			batchSize = getNextBatchSize( batchSize );
		}
		return result;
	}

	private static int getNextBatchSize(int batchSize) {
		if ( batchSize <= 10 ) {
			return batchSize - 1; //allow 9,8,7,6,5,4,3,2,1
		}
		else if ( batchSize / 2 < 10 ) {
			return 10;
		}
		else {
			return batchSize / 2;
		}
	}

	private static int SEED = 23;
	private static int PRIME_NUMER = 37;

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(Object[] array) {
		int seed = SEED;
		for ( Object anArray : array ) {
			seed = hash( seed, anArray == null ? 0 : anArray.hashCode() );
		}
		return seed;
	}

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(char[] array) {
		int seed = SEED;
		for ( char anArray : array ) {
			seed = hash( seed, anArray );
		}
		return seed;
	}

	/**
	 * calculate the array hash (only the first level)
	 */
	public static int hash(byte[] bytes) {
		int seed = SEED;
		for ( byte aByte : bytes ) {
			seed = hash( seed, aByte );
		}
		return seed;
	}

	private static int hash(int seed, int i) {
		return PRIME_NUMER * seed + i;
	}

	/**
	 * Compare 2 arrays only at the first level
	 *
	 * @deprecated Use {@link java.util.Arrays#equals(Object[], Object[])} instead
	 */
	@Deprecated
	public static boolean isEquals(Object[] o1, Object[] o2) {
		if ( o1 == o2 ) {
			return true;
		}
		if ( o1 == null || o2 == null ) {
			return false;
		}
		int length = o1.length;
		if ( length != o2.length ) {
			return false;
		}
		for ( int index = 0; index < length; index++ ) {
			if ( !o1[index].equals( o2[index] ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compare 2 arrays only at the first level
	 *
	 * @deprecated Use {@link java.util.Arrays#equals(char[], char[])} instead
	 */
	@Deprecated
	public static boolean isEquals(char[] o1, char[] o2) {
		if ( o1 == o2 ) {
			return true;
		}
		if ( o1 == null || o2 == null ) {
			return false;
		}
		int length = o1.length;
		if ( length != o2.length ) {
			return false;
		}
		for ( int index = 0; index < length; index++ ) {
			if ( !( o1[index] == o2[index] ) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Compare 2 arrays only at the first level
	 *
	 * @deprecated Use {@link java.util.Arrays#equals(byte[], byte[])} instead
	 */
	@Deprecated
	public static boolean isEquals(byte[] b1, byte[] b2) {
		if ( b1 == b2 ) {
			return true;
		}
		if ( b1 == null || b2 == null ) {
			return false;
		}
		int length = b1.length;
		if ( length != b2.length ) {
			return false;
		}
		for ( int index = 0; index < length; index++ ) {
			if ( !( b1[index] == b2[index] ) ) {
				return false;
			}
		}
		return true;
	}

	public static Serializable[] extractNonNull(Serializable[] array) {
		final int nonNullCount = countNonNull( array );
		final Serializable[] result = new Serializable[nonNullCount];
		int i = 0;
		for ( Serializable element : array ) {
			if ( element != null ) {
				result[i++] = element;
			}
		}
		if ( i != nonNullCount ) {
			throw new HibernateException( "Number of non-null elements varied between iterations" );
		}
		return result;
	}

	public static int countNonNull(Serializable[] array) {
		int i = 0;
		for ( Serializable element : array ) {
			if ( element != null ) {
				i++;
			}
		}
		return i;
	}

	public static String[] reverse(String[] source) {
		final int length = source.length;
		final String[] destination = new String[length];
		for ( int i = 0; i < length; i++ ) {
			final int x = length - i - 1;
			destination[x] = source[i];
		}
		return destination;
	}

	public static int[] trim(int[] from, int length) {
		int[] trimmed = new int[length];
		System.arraycopy( from, 0, trimmed, 0, length );
		return trimmed;
	}

	public static <T> List<T> toExpandableList(T[] values) {
		if ( values == null ) {
			return new ArrayList<T>();
		}
		return Arrays.asList( values );
	}

	@AllowSysOut
	public static void main(String... args) {
		int[] batchSizes = ArrayHelper.getBatchSizes( 32 );

		System.out.println( "Forward ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		for ( int i = 0; i < batchSizes.length; i++ ) {
			System.out.println( "[" + i + "] -> " + batchSizes[i] );
		}

		System.out.println( "Backward ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		for ( int i = batchSizes.length - 1; i >= 0; i-- ) {
			System.out.println( "[" + i + "] -> " + batchSizes[i] );
		}
	}

	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
}
