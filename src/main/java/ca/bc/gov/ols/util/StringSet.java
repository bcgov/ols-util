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
