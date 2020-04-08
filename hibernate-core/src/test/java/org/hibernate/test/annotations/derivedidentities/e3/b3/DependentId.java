package org.hibernate.test.annotations.derivedidentities.e3.b3;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class DependentId implements Serializable {
	@Column(length = 32)
	String name;
	EmployeeId empPK;
}
