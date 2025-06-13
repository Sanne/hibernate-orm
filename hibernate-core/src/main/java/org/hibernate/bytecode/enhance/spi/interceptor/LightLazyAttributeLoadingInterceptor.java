/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collections;
import java.util.Set;

/**
 * Lightweight version of {@link LazyAttributeLoadingInterceptor} : to be used
 * for entities which have no lazy attributes.
 */
public final class LightLazyAttributeLoadingInterceptor extends AbstractLazyLoadInterceptor {

	private final Object identifier;

	public LightLazyAttributeLoadingInterceptor(
			String entityName,
			Object identifier,
			SharedSessionContractImplementor session) {
		super( entityName, session );
		this.identifier = identifier;
	}

	@Override
	public Object getIdentifier() {
		return identifier;
	}

	@Override
	protected Object handleRead(Object target, String attributeName, Object value) {
		return value;
	}

	@Override
	protected Object handleWrite(Object target, String attributeName, Object oldValue, Object newValue) {
		return newValue;
	}

	public boolean isAttributeLoaded(String fieldName) {
		return true;
	}

	public boolean hasAnyUninitializedAttributes() {
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(entityName=" + getEntityName() + " ,lazyFields=[])";
	}

	@Override
	public void attributeInitialized(String name) {
	}

	@Override
	public Set<String> getInitializedLazyAttributeNames() {
		return Collections.emptySet();
	}

}
