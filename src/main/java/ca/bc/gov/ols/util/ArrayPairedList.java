/**
 * Copyright © 2008-2019, Province of British Columbia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
