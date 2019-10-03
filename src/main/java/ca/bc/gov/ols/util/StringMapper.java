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

import gnu.trove.map.hash.THashMap;

import java.util.Map;
import java.util.Set;

public class StringMapper
{
	private Map<String, String> map = new THashMap<String, String>();
	
	public StringMapper()
	{
		
	}
	
	public void putValues(String[] value)
	{
		for(String s : value) {
			map.put(s, s);
		}
	}
	
	public void put(String key, String value)
	{
		map.put(key, value);
	}
	
	public Set<String> keySet()
	{
		return map.keySet();
	}
	
	/**
	 * Gets the mapped value for a key in the map
	 * 
	 * @param key the key to retrieve
	 * @return the value for the key, if it exists or null if the key does not exist.
	 */
	public String get(String key)
	{
		return map.get(key);
	}
	
	/**
	 * Replaces a mapped value if it exists
	 * 
	 * @param key the value to replace
	 * @return the value for the key, if it exists, or the key itself
	 */
	public String replace(String key)
	{
		String val = map.get(key);
		if(val != null) {
			return val;
		}
		return key;
	}
}
