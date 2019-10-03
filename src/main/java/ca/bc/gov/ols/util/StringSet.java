/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
 */
package ca.bc.gov.ols.util;

import gnu.trove.set.hash.THashSet;

import java.util.Collection;
import java.util.Set;

/**
 * A set of Strings allowing determination of membership. 
 * 
 * @author mbdavis
 *
 */
public class StringSet 
{
	/**
	 * HashSet lookup is MUCH faster than using RegEx (> 10x)
	 */
	private Set<String> keys = new THashSet<String>();
	
	public StringSet(Collection<String> vals)
	{
		keys.addAll(vals);
	}
	
	public StringSet(String[] vals)
	{
		init(vals);
	}
	
	public void init(String[] vals)
	{
		for (String s : vals) {
			keys.add(s);
		}
	}
	
	public boolean contains(String s)
	{
		return keys.contains(s);
	}
}
