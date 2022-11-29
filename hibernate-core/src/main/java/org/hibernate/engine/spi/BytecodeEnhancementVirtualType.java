/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.engine.spi;

public interface BytecodeEnhancementVirtualType {

	default ManagedEntity asManagedEntity() {
		return null;
	}

	default PersistentAttributeInterceptable asPersistentAttributeInterceptable() {
		return null;
	}

	default SelfDirtinessTracker asSelfDirtinessTracker() {
		return null;
	}

	default Managed asManaged() {
		return null;
	}

	default ManagedComposite asManagedComposite() {
		return null;
	}

	default ManagedMappedSuperclass asManagedMappedSuperclass() {
		return null;
	}

}
