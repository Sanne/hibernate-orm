/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

//$Id$
package org.hibernate.test.annotations.collectionelement;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;

import org.hibernate.test.annotations.Country;

/**
 * @author Emmanuel Bernard
 */
@Embeddable
public class CountryAttitude {
	private Boy boy;
	private Country country;
	private boolean likes;

	// TODO: This currently does not work
//	@ManyToOne(optional = false)
//	public Boy getBoy() {
//		return boy;
//	}

	public void setBoy(Boy boy) {
		this.boy = boy;
	}

	@ManyToOne(optional = false)
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name = "b_likes")
	public boolean isLikes() {
		return likes;
	}

	public void setLikes(boolean likes) {
		this.likes = likes;
	}

	@Override
	public int hashCode() {
		return country.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( !( obj instanceof CountryAttitude ) ) {
			return false;
		}
		return country.equals( ( (CountryAttitude) obj ).country );
	}
}
