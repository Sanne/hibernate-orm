/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.mapping;

import static jakarta.persistence.CascadeType.ALL;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "CONF")
@IdClass(ConfId.class)
public class ConfEntity implements Serializable{

	private static final long serialVersionUID = -5089484717715507169L;

	@Id
	@Column(name = "confKey")
	private String confKey;

	@Id
	@Column(name = "confValue")
	private String confValue;

	@OneToMany(mappedBy="conf", cascade = ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<UserConfEntity> userConf = new HashSet<UserConfEntity>();
	
	public String getConfKey() {
		return confKey;
	}

	public void setConfKey(String confKey) {
		this.confKey = confKey;
	}

	public String getConfValue() {
		return confValue;
	}

	public void setConfValue(String confValue) {
		this.confValue = confValue;
	}

	public Set<UserConfEntity> getUserConf() {
		return userConf;
	}
}
