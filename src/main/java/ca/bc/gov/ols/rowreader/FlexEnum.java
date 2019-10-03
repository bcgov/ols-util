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
package ca.bc.gov.ols.rowreader;

import gnu.trove.impl.Constants;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FlexEnum allows you to use integers to keep track of string values in multiple String-named namespaces.
 * This is to allow an enumeration type of model for things which are enumerated at runtime, instead of statically.
 *
 * @author chodgson
 *
 */
public class FlexEnum {
	private static Map<String,FlexEnum> enumDefs = new THashMap<String,FlexEnum>();

	private TObjectIntHashMap<String> nameToIdMap = new TObjectIntHashMap<String>(Constants.DEFAULT_CAPACITY, Constants.DEFAULT_LOAD_FACTOR, Integer.MIN_VALUE);
	private List<String> idToNameMap = new ArrayList<String>();
	private boolean locked = false;
		
	public static int enumVal(String type, String value) {
	 
		FlexEnum enumDef = enumDefs.get("type");
		if(enumDef == null) {
			enumDef = new FlexEnum();
			enumDefs.put(type, enumDef);
		}
		return enumDef.enumVal(value);
	}
	
	public static String valueOf(String type, int enumVal) {
		FlexEnum enumDef = enumDefs.get("type");
		if(enumDef == null) {
			return null;
		}
		try {
			return enumDef.valueOf(enumVal);	
		} catch(IndexOutOfBoundsException ioobe) {
			throw new IllegalArgumentException("Illegal references to unknown enumeration value '" 
					+ enumVal + "' of type '" + type + "'", ioobe);
		}
	}
	
	public static void lock(String type) {
		FlexEnum enumDef = enumDefs.get("type");
		if(enumDef == null) {
			throw new IllegalArgumentException("Illegal reference to unknown enumeration type '" + type + "'");
		}
		enumDef.lock();
	}
	
	public int enumVal(String value) {
		int enumVal = nameToIdMap.get(value);
		if(enumVal == Integer.MIN_VALUE) {
			if(locked) {
				throw new IllegalArgumentException("Illegal reference to unknown enumeration value '" 
						+ value + "'");				
			} else {
				enumVal = idToNameMap.size();
				idToNameMap.add(value);
				nameToIdMap.put(value, enumVal);
			}
		}
		return enumVal;
	}
	
	public String valueOf(int enumVal) {
		return idToNameMap.get(enumVal);
	}
	
	public void lock() {
		locked = true;
	}
	
}