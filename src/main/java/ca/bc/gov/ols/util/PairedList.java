/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

import java.util.Iterator;

public interface PairedList<T, V> extends Iterable<PairedListEntry<T, V>> {
	
	void add(T left, V right);
	
	@Override
	Iterator<PairedListEntry<T, V>> iterator();
	
	void trimToSize();
	
}
