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

import gnu.trove.map.hash.THashMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FlexObj {

	private final Map<String,Integer> schema;
	private final Object[] data;
	
	public FlexObj(Map<String,Integer> schema) {
		this.schema = schema;
		this.data = new Object[schema.size()];
	}
	
	public FlexObj(Map<String,Integer> schema, Object[] data) {
		this.schema = schema;
		this.data = data;
	}

	public Object get(String field) {
		return data[schema.get(field)];
	}
	
	public void set(String field, Object value) {
		data[schema.get(field)] = value;
	}
	
	public static Map<String,Integer> createSchema(String[] fields) {
		return createSchema(Arrays.asList(fields));
	}
	
	public static Map<String,Integer> createSchema(List<String> fields) {
		Map<String,Integer> schema = new THashMap<String,Integer>();
		for(int i = 0; i < fields.size(); i++) {
			schema.put(fields.get(i), i);
		}
		return schema; 
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("FlexObj:{");
		for(int i = 0; i < data.length; i++) {
			if(i > 0) {
				sb.append(", ");
			}
			for(Entry<String,Integer> entry : schema.entrySet()) {
				if(entry.getValue() == i) {
					sb.append(entry.getKey() + ":");
					break;
				}
			}
			sb.append(data[i]);
		}
		return sb.toString();
	}
}
