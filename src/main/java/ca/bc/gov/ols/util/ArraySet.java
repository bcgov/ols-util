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
