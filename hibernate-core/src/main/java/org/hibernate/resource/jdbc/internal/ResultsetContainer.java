/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.resource.jdbc.internal;

import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * We normally record Statement(s) and their associated ResultSet(s) in a Map
 * to guarantee proper resource cleanup, however such types will commonly
 * be implemented in a way to require identity hashcode calculations, which has
 * been shown to become a drag on overall system efficiency
 * (There are JVM tunables one can use to improve on the default, but they have
 * system wide impact which in turn could have undesirable impact on other libraries).
 * As in the most common case we process a single statement at a time, we
 * trade some code complexity here by attempting to keep track of such resources
 * via direct fields only, and overflow to the normal Map usage for the less
 * common cases.
 */
final class ResultsetContainer {

	//Implementation notes:
	// # if key_1 is non-null, then value_1 is the value it maps to.
	// # if key_1 is null, then the Map in xref is guaranteed to be empty
	// # The Map in xref is lazily initialized, but when emptied it's not guaranteed to be made null

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
		return key_1 != null; //No need to check the content of xref.
	}

	public void registerExpectingNew(final Statement statement) {
		//We use an assert here as it's a relatively expensive check and I'm fairly confident this would never happen at runtime:
		//the assertion is useful to keep this confidence by leveraging the testsuite.
		assert statementNotExisting( statement ) : "JDBC Statement already registered";
		if ( key_1 == null ) {
			//this is the fast-path: most likely case and most efficient as we avoid accessing xref altogether
			key_1 = statement;
			value_1 = EMPTY;
		}
		else {
			getXrefForWriting().put( statement, EMPTY );
		}
	}

	//Assertion helper only:
	private boolean statementNotExisting(final Statement statement) {
		if ( key_1 == statement ) {
			return false;
		}
		else if ( xref != null ) {
			return ! xref.containsKey( statement );
		}
		else {
			return true;
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
			trickleDown(); //most expensive operation, but necessary to guarantee the invariants which allow the other optimisations
			return v;
		}
		else if ( xref != null ) {
			return xref.remove( statement );
		}
		return null;
	}

	private void trickleDown() {
		//Moves the first entry from the xref map into the fields, if any entry exists in it.
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
		else if ( key_1 != null && xref != null ) {
			existingEntry = xref.get( statement );
		}
		else {
			existingEntry = null;
		}

		//A debug warning wrapped in an assertion to avoid its overhead in production systems
		assert warnOnNotNull( existingEntry );

		final HashMap<ResultSet,Object> writeableEntry;
		if ( existingEntry == EMPTY || existingEntry == null ) {
			writeableEntry = new HashMap<>();
			directPut( statement, writeableEntry );
		}
		else {
			writeableEntry = existingEntry;
		}
		return writeableEntry;
	}

	private boolean warnOnNotNull(HashMap<ResultSet, Object> existingEntry) {
		// Keep this at DEBUG level, rather than warn.  Numerous connection pool implementations can return a
		// proxy/wrapper around the JDBC Statement, causing excessive logging here.  See HHH-8210.
		if ( existingEntry == null ) {
			log.debug( "ResultSet statement was not registered (on register)" );
		}
		return true;
	}

	private void directPut(final Statement statement, HashMap<ResultSet, Object> entry) {
		if ( key_1 == statement ) {
			value_1 = entry;
		}
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
			if ( xref != null ) {
				xref.forEach( action );
			}
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
