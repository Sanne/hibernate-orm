/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.querycache;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Book")
public class Book {
	@Id
	private Long id;
	private String title;
	private String isbn;
	private String author;
	private Integer year;
	private String category;
	private Double price;
	private String status;

	// Constructors
	public Book() {}

	public Book(Long id, String title, String isbn) {
		this.id = id;
		this.title = title;
		this.isbn = isbn;
	}

	// Getters and setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }

	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }

	public String getIsbn() { return isbn; }
	public void setIsbn(String isbn) { this.isbn = isbn; }

	public String getAuthor() { return author; }
	public void setAuthor(String author) { this.author = author; }

	public Integer getYear() { return year; }
	public void setYear(Integer year) { this.year = year; }

	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }

	public Double getPrice() { return price; }
	public void setPrice(Double price) { this.price = price; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }
}
