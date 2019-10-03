/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

public class PairedListEntry<T, V> {
	T left;
	V right;
	
	public PairedListEntry(T left, V right) {
		this.left = left;
		this.right = right;
	}
	
	public T getLeft() {
		return left;
	}
	
	public V getRight() {
		return right;
	}
}
