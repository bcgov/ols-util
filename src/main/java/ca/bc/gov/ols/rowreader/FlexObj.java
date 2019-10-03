/**
 * Copyright 2008-2015, Province of British Columbia
 * All rights reserved.
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
