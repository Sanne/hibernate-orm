/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.cache.spi;

import java.io.Serializable;

import org.hibernate.engine.spi.EntityKey;
import org.hibernate.persister.entity.EntityPersister;

/**
 * Allows multiple entity roles to be stored in the same cache region. Also allows for composite
 * keys which do not properly implement equals()/hashCode().
 *
 * @author Gavin King
 * @author Steve Ebersole
 */
public interface EntityCacheKey extends CacheKey {

	public Serializable getKey();

	public String getEntityOrRoleName();

	public String getTenantId();

	public EntityKey toEntityKey(EntityPersister subclassPersister);

}
