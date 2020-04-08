/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.event.collection.detached;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Steve Ebersole
 */
@Entity
@Table(name="`Alias`")
public class Alias implements Identifiable {
	private Integer id;
	private String alias;
	private List<Character> characters = new ArrayList<Character>();

	public Alias() {
	}

	public Alias(Integer id, String alias) {
		this.id = id;
		this.alias = alias;
	}

	@Id
	@Override
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="`alias`")
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@ManyToMany( cascade = CascadeType.ALL )
	@JoinTable( name = "CHARACTER_ALIAS", indexes = @Index( columnList = "characters_id"))
//	@JoinTable(
//			name = "CHARACTER_ALIAS",
//			joinColumns = @JoinColumn(name="ALIAS_ID", referencedColumnName="ID"),
//			inverseJoinColumns = @JoinColumn(name="CHARACTER_ID", referencedColumnName="ID")
//	)
	public List<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(List<Character> characters) {
		this.characters = characters;
	}

}
