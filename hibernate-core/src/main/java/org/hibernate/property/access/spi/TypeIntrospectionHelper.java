/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property.access.spi;

import org.checkerframework.checker.nullness.qual.NonNull;

import org.hibernate.internal.util.ReflectHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

import static org.hibernate.internal.util.NullnessUtil.castNonNull;

/**
 * Cache for class#getDeclaredMethods() : we invoke this very frequently during
 * metadata building, and it's been shown to have a significant impact on memory
 * consumption for those models that have many attributes.
 */
public final class TypeIntrospectionHelper {

	private final Class clazz;
	private final boolean isRecord;

	private Method[] nonStaticDeclaredMethods; //lazy but try to reuse

	private TypeIntrospectionHelper[] directSuperInterfaces;

	private TypeIntrospectionHelper directSuperType;

	public TypeIntrospectionHelper(final Class<?> type) {
		this.clazz = Objects.requireNonNull( type );
		this.isRecord = ReflectHelper.isRecord( type );
	}

	public static TypeIntrospectionHelper fromType(Class<?> containerClass) {
		return new TypeIntrospectionHelper( containerClass );
	}

	public Class<?> getType() {
		return clazz;
	}

	public String getName() {
		return clazz.getName();
	}

	public Field getDeclaredField(String name) throws NoSuchFieldException {
		return clazz.getDeclaredField( name );
	}

	public TypeIntrospectionHelper getHelperForSuperclass() {
		if ( directSuperType == null ) {
			final Class superclass = clazz.getSuperclass();
			if ( superclass != null ) {
				directSuperType = new TypeIntrospectionHelper( superclass );
			}
		}
		return directSuperType;
	}

	public Method[] getNonStaticDeclaredMethods() {
		if ( nonStaticDeclaredMethods == null ) {
			nonStaticDeclaredMethods = nonStaticFilter( clazz.getDeclaredMethods() );
		}
		return nonStaticDeclaredMethods;
	}

	private static Method[] nonStaticFilter(@NonNull final Method[] methods) {
		boolean staticMethodFound = false;
		for ( Method method : methods ) {
			if ( Modifier.isStatic( method.getModifiers() ) ) {
				staticMethodFound = true;
				break;
			}
		}
		//Many entities won't have any static method: avoid allocations for the common case.
		if ( staticMethodFound ) {
			@NonNull Method[] output = new Method[methods.length - 1];//-1 as we know at least one is going to be a static method
			int idx = 0;
			for ( Method method : methods ) {
				if ( !Modifier.isStatic( method.getModifiers() ) ) {
					output[idx++] = method;
				}
			}
			return castNonNull( Arrays.copyOf( output, idx ) );
		}
		else {
			return methods;
		}
	}

	public boolean isRecord() {
		return isRecord;
	}

	public boolean isObject() {
		return Object.class.equals( this.clazz );
	}

	public TypeIntrospectionHelper[] getInterfaces() {
		if ( directSuperInterfaces == null ) {
			final Class[] interfaces = clazz.getInterfaces();
			//TODO: filter out marker interfaces we've added ourselves via bytecode enhancement?
			final TypeIntrospectionHelper[] ifs = new TypeIntrospectionHelper[interfaces.length];
			for ( int i = 0; i < interfaces.length; i++ ) {
				ifs[i] = new TypeIntrospectionHelper( interfaces[i] );
			}
			directSuperInterfaces = ifs;
		}
		return directSuperInterfaces;
	}

	public Method getMethod(String propertyName, Class<?>[] paramSignature) throws NoSuchMethodException {
		return clazz.getMethod( propertyName, paramSignature );
	}

	public Method getDeclaredMethod(String name) throws NoSuchMethodException {
		return clazz.getDeclaredMethod( name );
	}

}
