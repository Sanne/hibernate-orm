package org.hibernate.query.internal;

import java.util.Objects;

public final class FullPath {

	private final FullPath parentFullPath;
	private final String navigableName;
	//TODO: check if it's worth also storing the hashcode of parentFullPath ?

	public FullPath(FullPath parentFullPath, String navigableName) {
		Objects.requireNonNull( parentFullPath );
		Objects.requireNonNull( navigableName );
		this.parentFullPath = parentFullPath;
		this.navigableName = navigableName;
	}

	public FullPath(String navigableName) {
		this.parentFullPath = null;
		this.navigableName = navigableName;
	}

	//This method is slow: make sure it's only used for debugging and diagnostics
	public String toString() {
		return parentFullPath == null ?
				navigableName :
				parentFullPath.toString() + '.' + navigableName;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || FullPath.class != o.getClass() ) {
			return false;
		}

		FullPath fullPath = (FullPath) o;

		if ( parentFullPath != null ?
				!parentFullPath.equals( fullPath.parentFullPath ) :
				fullPath.parentFullPath != null ) {
			return false;
		}
		return navigableName.equals( fullPath.navigableName );
	}

	@Override
	public int hashCode() {
		int result = parentFullPath != null ? parentFullPath.hashCode() : 0;
		result = 31 * result + navigableName.hashCode();
		return result;
	}

	public static boolean isEmpty(final FullPath fp) {
		//FIXME ?
		return (fp == null) || fp.navigableName == null;
	}


}
