package org.hibernate.metamodel.mapping;

import java.util.Iterator;

public interface ReadOnlyList<T> {
	Iterator<T> iterator();

	T get(int index);

	int size();
}
