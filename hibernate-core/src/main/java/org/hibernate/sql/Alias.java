/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.sql;
import org.hibernate.dialect.Dialect;

/**
 * An alias generator for SQL identifiers
 * @author Gavin King
 */
public final class Alias {

	private final int length;
	private final String suffix;
	private final int suffixLength;

	/**
	 * Constructor for Alias.
	 */
	public Alias(int length, String suffix) {
		this.suffixLength = ( suffix == null ) ? 0 : suffix.length();
		this.length = length - suffixLength;
		this.suffix = suffix;
	}

	/**
	 * Constructor for Alias.
	 */
	public Alias(String suffix) {
		this.length = Integer.MAX_VALUE;
		this.suffix = suffix;
		this.suffixLength = ( suffix == null ) ? 0 : suffix.length();
	}

	public String toAliasString(String sqlIdentifier) {
		char begin = sqlIdentifier.charAt(0);
		int quoteType = Dialect.QUOTE.indexOf(begin);
		String unquoted = getUnquotedAliasString(sqlIdentifier, quoteType);
		if ( quoteType >= 0 ) {
			char endQuote = Dialect.CLOSED_QUOTE.charAt(quoteType);
			return begin + unquoted + endQuote;
		}
		else {
			return unquoted;
		}
	}

	public String toUnquotedAliasString(String sqlIdentifier) {
		return getUnquotedAliasString(sqlIdentifier);
	}

	private String getUnquotedAliasString(String sqlIdentifier) {
		char begin = sqlIdentifier.charAt(0);
		int quoteType = Dialect.QUOTE.indexOf(begin);
		return getUnquotedAliasString(sqlIdentifier, quoteType);
	}

	private String getUnquotedAliasString(final String sqlIdentifier, final int quoteType) {
		if ( quoteType < 0 && this.suffixLength == 0 ) {
			//shortcut:
			return sqlIdentifier;
		}
		final int startIdx = quoteType >= 0 ? 1 : 0;
		final int endIdx = quoteType >= 0 ? Math.min( sqlIdentifier.length() - 1, length ) : Math.min( sqlIdentifier.length(), length );
		StringBuilder sb = new StringBuilder( this.suffixLength + endIdx );
		if ( suffix != null ) {
			sb.append( suffix );
		}
		sb.append( sqlIdentifier, startIdx, endIdx );
		return sb.toString();
	}

	public String[] toUnquotedAliasStrings(String[] sqlIdentifiers) {
		String[] aliases = new String[ sqlIdentifiers.length ];
		for ( int i=0; i<sqlIdentifiers.length; i++ ) {
			aliases[i] = toUnquotedAliasString(sqlIdentifiers[i]);
		}
		return aliases;
	}

	public String[] toAliasStrings(String[] sqlIdentifiers) {
		String[] aliases = new String[ sqlIdentifiers.length ];
		for ( int i=0; i<sqlIdentifiers.length; i++ ) {
			aliases[i] = toAliasString(sqlIdentifiers[i]);
		}
		return aliases;
	}

}
