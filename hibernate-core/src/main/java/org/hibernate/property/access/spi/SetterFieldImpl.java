/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

import org.hibernate.Internal;
import org.hibernate.PropertyAccessException;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.AbstractFieldSerialForm;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Field-based implementation of Setter
 *
 * @author Steve Ebersole
 */
@Internal
public class SetterFieldImpl implements Setter {
	private final TypeIntrospectionHelper typeWrapper;
	private final String propertyName;
	private final Field field;
	private final @Nullable Method setterMethod;

	public SetterFieldImpl(TypeIntrospectionHelper typeWrapper, String propertyName, Field field) {
		this.typeWrapper = typeWrapper;
		this.propertyName = propertyName;
		this.field = field;
		this.setterMethod = ReflectHelper.setterMethodOrNull( typeWrapper, propertyName, field.getType() );
	}

	public Class<?> getContainerClass() {
		return typeWrapper.getType();
	}

	public String getPropertyName() {
		return propertyName;
	}

	public Field getField() {
		return field;
	}

	@Override
	public void set(Object target, @Nullable Object value) {
		try {
			field.set( target, value );
		}
		catch (Exception e) {
			if (value == null && field.getType().isPrimitive()) {
				throw new PropertyAccessException(
						e,
						String.format(
								Locale.ROOT,
								"Null value was assigned to a property [%s.%s] of primitive type",
								typeWrapper,
								propertyName
						),
						true,
						typeWrapper.getType(),
						propertyName
				);
			}
			else {
				final String valueType;
				final LazyInitializer lazyInitializer = HibernateProxy.extractLazyInitializer( value );
				if ( lazyInitializer != null ) {
					valueType = lazyInitializer.getEntityName();
				}
				else if ( value != null ) {
					valueType = value.getClass().getTypeName();
				}
				else {
					valueType = "<unknown>";
				}
				throw new PropertyAccessException(
						e,
						String.format(
								Locale.ROOT,
								"Could not set value of type [%s]",
								valueType
						),
						true,
						typeWrapper.getType(),
						propertyName
				);
			}
		}
	}

	@Override
	public @Nullable String getMethodName() {
		return setterMethod != null ? setterMethod.getName() : null;
	}

	@Override
	public @Nullable Method getMethod() {
		return setterMethod;
	}

	private Object writeReplace() {
		return new SerialForm( typeWrapper.getType(), propertyName, field );
	}

	private static class SerialForm extends AbstractFieldSerialForm implements Serializable {
		private final Class<?> containerClass;
		private final String propertyName;


		private SerialForm(Class<?> containerClass, String propertyName, Field field) {
			super( field );
			this.containerClass = containerClass;
			this.propertyName = propertyName;
		}

		private Object readResolve() {
			return new SetterFieldImpl( TypeIntrospectionHelper.fromType( containerClass ), propertyName, resolveField() );
		}

	}
}
