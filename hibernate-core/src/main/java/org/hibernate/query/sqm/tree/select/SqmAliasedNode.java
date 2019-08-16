/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.query.sqm.tree.select;

import org.hibernate.query.sqm.SqmExpressable;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.query.sqm.sql.internal.DomainResultProducer;

/**
 * Models any aliased expression.  E.g. `select exp as e ...`
 * where  the aliased node is the complete `exp as e` "expression" -
 * `exp` is it's "wrapped node" and `e` is the alias.
 *
 * This will only ever be some kind of selection (dynamic-instantiation,
 * JPA select-object syntax, an expression or a dynamic-instantiation
 * argument).  Each of these can be represented as a
 * {@link DomainResultProducer} which is ultimately
 *
 * @author Steve Ebersole
 */
public interface SqmAliasedNode<T> extends SqmTypedNode<T> {
	SqmSelectableNode<T> getSelectableNode();
	String getAlias();

	@Override
	default SqmExpressable<T> getNodeType() {
		return getSelectableNode().getNodeType();
	}
}
