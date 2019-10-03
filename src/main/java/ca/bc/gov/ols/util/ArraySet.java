/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * This is a trivial implementation of a set using an ArrayList. The idea is that for relatively
 * small sets, and/or sets where the insertion and search time aren't as important as being able to
 * minimize memory usage (with trimToSize()); Such a set might be just called a "unique list".
 * 
 * @author chodgson
 * 
 * @param <E> The type of object stored in the set
 */
public class ArraySet<E> extends ArrayList<E> implements Set<E> {
	
	private static final long serialVersionUID = 1L;
	
	public ArraySet() {
		super();
	}

	public ArraySet(int initialCapacity) {
		super(initialCapacity);
	}
	
	@Override
	public boolean add(E e) {
		if(contains(e)) {
			return false;
		}
		return super.add(e);
	}
	
	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException("ArraySet doesn't support adding at an index.");
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		for(E e : c) {
			changed |= add(e);
		}
		return changed;
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException("ArraySet doesn't support adding at an index.");
	}
	
}
