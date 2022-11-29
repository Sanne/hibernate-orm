/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.spi;

/**
 * @author Steve Ebersole
 */
public interface PersistentAttributeInterceptable extends BytecodeEnhancementVirtualType {
	PersistentAttributeInterceptor $$_hibernate_getInterceptor();
	void $$_hibernate_setInterceptor(PersistentAttributeInterceptor interceptor);

	@Override
	default PersistentAttributeInterceptable asPersistentAttributeInterceptable() {
		return this;
	}

}
