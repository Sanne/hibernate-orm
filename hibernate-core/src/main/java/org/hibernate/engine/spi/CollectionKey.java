/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.engine.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.hibernate.EntityMode;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.Type;

/**
 * Uniquely identifies a collection instance in a particular session.
 *
 * @author Gavin King
 * @author Sanne Grinovero
 */
public final class CollectionKey implements Serializable {

	private final CollectionPersister persister;
	private final Serializable key;
	private final int hashCode;

	public CollectionKey(CollectionPersister persister, Serializable key) {
		this.persister = persister;
		this.key = key;
		this.hashCode = persister.collectionKeyHashcode( key );
	}

	/**
	 * The EntityMode parameter is now ignored. Use the other constructor.
	 * @deprecated Use {@link #CollectionKey(CollectionPersister, Serializable)}
	 */
	@Deprecated
	public CollectionKey(CollectionPersister persister, Serializable key, EntityMode em) {
		this( persister, key );
	}

	public String getRole() {
		return persister.getRole();
	}

	public Serializable getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "CollectionKey"
				+ MessageHelper.collectionInfoString( persister, key, persister.getFactory() );
	}

	@Override
	public boolean equals(Object other) {
		if ( this == other ) {
			return true;
		}
		if ( other == null || CollectionKey.class != other.getClass() ) {
			return false;
		}

		final CollectionKey that = (CollectionKey) other;
		return that.persister.equals( this.persister ) && this.persister.collectionKeyEquals( this.key, that.key );
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Custom serialization routine used during serialization of a
	 * Session/PersistenceContext for increased performance.
	 *
	 * @param oos The stream to which we should write the serial data.
	 *
	 * @throws java.io.IOException
	 */
	public void serialize(ObjectOutputStream oos) throws IOException {
		oos.writeObject( persister.getRole() );
		oos.writeObject( key );
	}

	/**
	 * Custom deserialization routine used during deserialization of a
	 * Session/PersistenceContext for increased performance.
	 *
	 * @param ois The stream from which to read the entry.
	 * @param session The session being deserialized.
	 *
	 * @return The deserialized CollectionKey
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static CollectionKey deserialize(
			ObjectInputStream ois,
			SessionImplementor session) throws IOException, ClassNotFoundException {
		return new CollectionKey(
				(String) ois.readObject(),
				(Serializable) ois.readObject(),
				(session == null ? null
				(session == null ? null
				(session == null ? null		: session.getFactory())
		);
	}
}
