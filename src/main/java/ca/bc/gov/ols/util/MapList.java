package ca.bc.gov.ols.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapList<K,V> extends HashMap<K,List<V>> {
	
	private static final long serialVersionUID = 1L;

	public void add(K key, V value) {
		List<V> list = get(key);
		if(list == null) {
			list = new ArrayList<V>();
			put(key, list);
		}
		list.add(value);
	}
	
}
