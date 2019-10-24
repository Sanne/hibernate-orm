/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.event.spi;

import java.util.Objects;
import java.io.Serializable;

/**
 * An event class for merge() and saveOrUpdateCopy()
 *
 * @author Gavin King
 */
public class MergeEvent extends AbstractEvent {

	private Object original;
	private String entityName;
	private Object entity;
	private Object result;

	public MergeEvent(String entityName, Object original, EventSource source) {
		this( original, source );
		this.entityName = entityName;
	}

	public MergeEvent(Object object, EventSource source) {
		super( source );
		Objects.requireNonNull( object );
		this.original = object;
	}

	public Object getOriginal() {
		return original;
	}

	public void setOriginal(Object object) {
		this.original = object;
	}

	/**
	 * @deprecated not longer used. Always returns null.
	 * @return returns null.
	 */
	@Deprecated
	public Serializable getRequestedId() {
		return null;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Object getEntity() {
		return entity;
	}
	public void setEntity(Object entity) {
		this.entity = entity;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
