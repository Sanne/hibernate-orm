/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import javax.persistence.metamodel.Attribute;

import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.metamodel.model.AttributeClassification;
import org.hibernate.metamodel.model.domain.DomainType;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

/**
 * Models the commonality of the JPA {@link Attribute} hierarchy.
 *
 * @param <D> The type of the class (D)eclaring this attribute
 * @param <J> The (J)ava type of this attribute
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAttribute<D,J,B>
		implements PersistentAttributeDescriptor<D,J,B>, Serializable {
	private final ManagedTypeDescriptor<D> declaringType;
	private final String name;
	private final JavaTypeDescriptor<J> attributeType;

	private final AttributeClassification attributeClassification;

	private final SimpleTypeDescriptor<B> valueType;
	private transient Member member;


	@SuppressWarnings("WeakerAccess")
	protected AbstractAttribute(
			ManagedTypeDescriptor<D> declaringType,
			String name,
			JavaTypeDescriptor<J> attributeType,
			AttributeClassification attributeClassification,
			SimpleTypeDescriptor<B> valueType,
			Member member) {
		this.declaringType = declaringType;
		this.name = name;
		this.attributeType = attributeType;
		this.attributeClassification = attributeClassification;
		this.valueType = valueType;
		this.member = member;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getNavigableName() {
		return getName();
	}

	@Override
	public Class<J> getJavaType() {
		return attributeType.getJavaType();
	}

	@Override
	public DomainType<B> getSqmNodeType() {
		return valueType;
	}

	@Override
	public JavaTypeDescriptor<J> getJavaTypeDescriptor() {
		return attributeType;
	}

	@Override
	public ManagedTypeDescriptor<D> getDeclaringType() {
		return declaringType;
	}

	@Override
	public Member getJavaMember() {
		return member;
	}

	@Override
	public AttributeClassification getAttributeClassification() {
		return attributeClassification;
	}

	@Override
	public PersistentAttributeType getPersistentAttributeType() {
		return getAttributeClassification().getJpaClassification();
	}

	@Override
	public SimpleTypeDescriptor<?> getValueGraphType() {
		return valueType;
	}

	@Override
	public String toString() {
		return declaringType.getName() + '#' + name + '(' + attributeClassification + ')';
	}

	/**
	 * Used by JDK serialization...
	 *
	 * @param ois The input stream from which we are being read...
	 * @throws java.io.IOException Indicates a general IO stream exception
	 * @throws ClassNotFoundException Indicates a class resolution issue
	 */
	@SuppressWarnings("unchecked")
	protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		final String memberDeclaringClassName = ( String ) ois.readObject();
		final String memberName = ( String ) ois.readObject();
		final String memberType = ( String ) ois.readObject();

		final Class memberDeclaringClass = Class.forName(
				memberDeclaringClassName,
				false,
				declaringType.getJavaType().getClassLoader()
		);
		try {
			this.member = "method".equals( memberType )
					? memberDeclaringClass.getMethod( memberName, ReflectHelper.NO_PARAM_SIGNATURE )
					: memberDeclaringClass.getField( memberName );
		}
		catch ( Exception e ) {
			throw new IllegalStateException(
					"Unable to locate member [" + memberDeclaringClassName + "#"
							+ memberName + "]"
			);
		}
	}

	/**
	 * Used by JDK serialization...
	 *
	 * @param oos The output stream to which we are being written...
	 * @throws IOException Indicates a general IO stream exception
	 */
	protected void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		oos.writeObject( getJavaMember().getDeclaringClass().getName() );
		oos.writeObject( getJavaMember().getName() );
		// should only ever be a field or the getter-method...
		oos.writeObject( Method.class.isInstance( getJavaMember() ) ? "method" : "field" );
	}
}
