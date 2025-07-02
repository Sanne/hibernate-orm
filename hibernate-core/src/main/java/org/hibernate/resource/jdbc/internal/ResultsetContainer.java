/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.resource.jdbc.internal;

import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

final class ResultsetContainer {

	private static final CoreMessageLogger log = CoreLogging.messageLogger( ResourceRegistryStandardImpl.class );

	//Used instead of Collections.EMPTY_SET to avoid polymorphic calls on xref;
	//Also, uses an HashMap as it were an HashSet, as technically we just need the Set semantics
	//but in this case the overhead of HashSet is not negligible.
	private static final HashMap<ResultSet,Object> EMPTY = new HashMap<>( 1, 0.2f );

	private Statement key_1;
	private HashMap<ResultSet,Object> value_1;

	//Additional pairs, for the case in which we need more:
	private HashMap<Statement, HashMap<ResultSet,Object>> xref;

	public boolean hasRegisteredResources() {
		return key_1 != null || ( xref != null && !xref.isEmpty() );
	}

	public void registerExpectingNew(final Statement statement) {
		final boolean duplicateRegistration;
		if ( key_1 == null ) {
			if ( xref == null ) {
				//this is the fast-path: most likely and most efficient as we avoid access to xref
				key_1 = statement;
				value_1 = EMPTY;
				duplicateRegistration = false;
			}
			else {
				key_1 = statement;
				value_1 = EMPTY;
				duplicateRegistration = xref.containsKey( statement );
			}
		}
		else {
			final HashMap<ResultSet,Object> previousValue = getXrefForWriting().putIfAbsent( statement, EMPTY );
			duplicateRegistration = previousValue != null;
		}
		if ( duplicateRegistration ) {
			throw new HibernateException( "JDBC Statement already registered" );
		}
	}

	private HashMap<Statement, HashMap<ResultSet,Object>> getXrefForWriting() {
		if ( this.xref == null ) {
			this.xref = new HashMap<>();
		}
		return this.xref;
	}

	public HashMap<ResultSet, Object> remove(final Statement statement) {
		if ( key_1 == statement ) {
			final HashMap<ResultSet, Object> v = value_1;
			key_1 = null;
			value_1 = null;
			trickleDown(); //uff...
			return v;
		}
		else if ( xref != null ) {
			return xref.remove( statement );
		}
		return null;
	}

	private void trickleDown() {
		//Moves the first entry from the map into the fields.
		if ( xref != null ) {
			Iterator<Map.Entry<Statement, HashMap<ResultSet, Object>>> iterator = xref.entrySet().iterator(); {
				if ( iterator.hasNext() ) {
					Map.Entry<Statement, HashMap<ResultSet, Object>> entry = iterator.next();
					key_1 = entry.getKey();
					value_1 = entry.getValue();
					iterator.remove();
				}
			}
		}
	}

	public HashMap<ResultSet, Object> get(Statement statement) {
		final HashMap<ResultSet,Object> existingEntry;
		if ( key_1 == statement ) {
			existingEntry = value_1;
		}
		else if ( xref != null ) {
			existingEntry = xref.get( statement );
		}
		else {
			existingEntry = null;
		}

		// Keep this at DEBUG level, rather than warn.  Numerous connection pool implementations can return a
		// proxy/wrapper around the JDBC Statement, causing excessive logging here.  See HHH-8210.
		if ( existingEntry == null ) {
			log.debug( "ResultSet statement was not registered (on register)" );
		}

		final HashMap<ResultSet,Object> writeableEntry;
		if ( existingEntry == null || existingEntry == EMPTY ) {
			writeableEntry = new HashMap<>();
			directPut( statement, writeableEntry );
		}
		else {
			writeableEntry = existingEntry;
		}
		return writeableEntry;
	}

	private void directPut(final Statement statement, HashMap<ResultSet, Object> entry) {
		if ( key_1 == statement ) {
			value_1 = entry;
		}
		//We strongly assume that if key1 is null then the map is empty (!)
		else if ( key_1 == null ) {
			key_1 = statement;
			value_1 = entry;
		}
		else {
			getXrefForWriting().put( statement, entry );
		}
	}

	public void forEach(final BiConsumer<Statement, HashMap<ResultSet, Object>> action) {
		if ( key_1 != null ) {
			action.accept( key_1, value_1 );
		}
		if ( xref != null ) {
			xref.forEach( action );
		}
	}

	public void clear() {
		key_1 = null;
		value_1 = null;
		if ( xref != null ) {
			xref.clear();
			xref = null;
		}
	}
}
