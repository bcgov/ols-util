/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayPairedList<T, V> implements PairedList<T, V> {
	
	int size;
	Object[] elementData;
	
	public ArrayPairedList() {
		this(10);
	}
	
	public ArrayPairedList(int size) {
		size = 0;
		elementData = new Object[size * 2];
	}
	
	public void ensureCapacity(int minCapacity) {
		int oldCapacity = elementData.length / 2;
		if(minCapacity > oldCapacity) {
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if(newCapacity < minCapacity) {
				newCapacity = minCapacity;
			}
			elementData = Arrays.copyOf(elementData, newCapacity * 2);
		}
	}
	
	@Override
	public void add(T left, V right) {
		ensureCapacity(size + 1);
		elementData[size * 2] = left;
		elementData[size * 2 + 1] = right;
		size++;
	}
	
	@Override
	public Iterator<PairedListEntry<T, V>> iterator() {
		return new Itr();
	}
	
	@Override
	public void trimToSize() {
		if(size < elementData.length / 2) {
			elementData = Arrays.copyOf(elementData, size * 2);
		}
	}
	
	public int size() {
		return size;
	}
	
	private class Itr implements Iterator<PairedListEntry<T, V>> {
		int cursor;
		
		@Override
		public boolean hasNext() {
			return cursor != size;
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public PairedListEntry<T, V> next() {
			if(cursor >= size) {
				throw new NoSuchElementException();
			}
			PairedListEntry<T, V> entry = new PairedListEntry<T, V>((T)elementData[cursor * 2],
					(V)elementData[cursor * 2 + 1]);
			cursor++;
			return entry;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
}
