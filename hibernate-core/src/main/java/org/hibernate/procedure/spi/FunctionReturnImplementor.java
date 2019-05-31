/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.procedure.spi;

import org.hibernate.procedure.FunctionReturn;
import org.hibernate.query.procedure.spi.ProcedureParameterImplementor;

/**
 * @author Steve Ebersole
 */
public interface FunctionReturnImplementor extends FunctionReturn, ProcedureParameterImplementor {
}
