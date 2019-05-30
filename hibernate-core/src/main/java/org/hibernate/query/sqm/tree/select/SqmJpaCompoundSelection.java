/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.tree.select;

import java.util.List;

import javax.persistence.metamodel.Type;

import org.hibernate.query.criteria.JpaCompoundSelection;
import org.hibernate.query.criteria.JpaSelection;
import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.SqmExpressable;
import org.hibernate.query.sqm.consume.spi.SemanticQueryWalker;
import org.hibernate.query.sqm.tree.expression.AbstractSqmExpression;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;

/**
 * @asciidoctor
 *
 * Models either a `Tuple` or `Object[]` selection as defined by the
 * JPA Criteria API.
 *
 * @author Steve Ebersole
 */
public class SqmJpaCompoundSelection<T>
		extends AbstractSqmExpression<T>
		implements JpaCompoundSelection<T>, SqmExpressable<T> {
	private final List<SqmSelectableNode<?>> selectableNodes;
	private final JavaTypeDescriptor<T> javaTypeDescriptor;

	public SqmJpaCompoundSelection(
			List<SqmSelectableNode<?>> selectableNodes,
			JavaTypeDescriptor<T> javaTypeDescriptor,
			NodeBuilder criteriaBuilder) {
		super( null, criteriaBuilder );
		this.selectableNodes = selectableNodes;
		this.javaTypeDescriptor = javaTypeDescriptor;

		setExpressableType( this );
	}

	@Override
	public JavaTypeDescriptor<T> getJavaTypeDescriptor() {
		return javaTypeDescriptor;
	}

	@Override
	public JavaTypeDescriptor<T> getExpressableJavaTypeDescriptor() {
		return getJavaTypeDescriptor();
	}

	@Override
	public Class<T> getJavaType() {
		return getJavaTypeDescriptor().getJavaType();
	}

	@Override
	public List<? extends JpaSelection<?>> getSelectionItems() {
		return selectableNodes;
	}

	@Override
	public JpaSelection<T> alias(String name) {
		return this;
	}

	@Override
	public String getAlias() {
		return null;
	}

	@Override
	public boolean isCompoundSelection() {
		return true;
	}

	@Override
	public <X> X accept(SemanticQueryWalker<X> walker) {
		return walker.visitJpaCompoundSelection( this );
	}
}
