/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.internal.util.cache;

import org.hibernate.service.Service;

public interface InternalCacheFactory extends Service {

	<K,V> InternalCache<K,V> createInternalCache(int intendedApproximateSize);

}
