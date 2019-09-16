package org.hibernate.test.propertynaming;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CART")
public class Cart {

	@Id
	@Column(name = "cart_id")
	private Long id;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,
			orphanRemoval = true)
	private List<Items> items;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Items> getItems() {
		return items;
	}

	public void setItems(List<Items> items) {
		this.items = items;
	}

}