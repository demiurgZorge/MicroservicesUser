package com.microservices.user.core.dao;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.microservices.user.core.interfaces.SortSpecification;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.interfaces.SortState.SortType;
import org.apache.commons.lang.StringUtils;

public class DataGlue {
	
	@SuppressWarnings("unchecked")
	public static <K,T> List<K> getFiledFromList(List<T> list, Class<T> entityType, String fieldName ) {
		try {
			String getter = "get" + StringUtils.capitalize(fieldName);
			Method m = entityType.getMethod(getter);
			List<K> result = new ArrayList<>();
			for(T t : list) {
				result.add((K)m.invoke(t));
			}
			return result;
		}
		catch(ReflectiveOperationException e) {
			return null;
		}
	}
	
	public static <K,T> Map<K,T> toMap(List<T> list, String keyField) {
		Map<K, T> result = new HashMap<>();
		if(list.size() == 0) {
			return result;
		}
		T obj = list.get(0);
		Class<?> cl = obj.getClass();
		String getter = "get" + StringUtils.capitalize(keyField);
		try {
			Method m = cl.getMethod(getter);
			for(T t : list) {
				@SuppressWarnings("unchecked")
				K key = (K) m.invoke(t);
				result.put(key, t);
			}
			return result;
		} catch (ReflectiveOperationException e) {
			return null;
		}
		
	}
	
	public static List<Long> toLong(List<?> list) {
		List<Long> result = new ArrayList<>();
		for(Object k : list) {
			result.add(((Integer)k).longValue());
		}
		return result;
	}
	
	public static <K> Map<Long, K> toLongKeys(Map<Integer, K> map) {
        Map<Long, K> result = new HashMap<>();
        for(Entry<Integer, K> k : map.entrySet()) {
            result.put(k.getKey().longValue(), k.getValue());
        }
        return result;
    }
	
	public static List<Integer> toInteger(List<Long> list) {
		List<Integer> result = new ArrayList<>();
		for(Long k : list) {
			result.add(k.intValue());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static <T, G> List<T> listCast(List<G> source) {
	    List<T> result = new ArrayList<>();
	    for(G g: source) {
	        result.add((T)g);
	    }
	    return result;
	}
	
	public static <K, V> Map<K, V> union(Map<K, V> map1, Map<K, V> map2) {
		Map<K, V> result = new HashMap<>(map1);
		result.putAll(map2);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> sort(List<T> list, QuerySpecification query) {
		SortState sort = query.getSorting();
		if (sort != null) {
			SortSpecification<T> sortSpec = (SortSpecification<T>) query.getSortSpecification(sort.getSortField());
			if (sortSpec instanceof InMemorySortSpecification<?>) {
				InMemorySortSpecification<T> inMemSort = (InMemorySortSpecification<T>)sortSpec;
				Comparator<T> comp = inMemSort.getComparator();
				List<T> result = new ArrayList<>(list);
				Collections.sort(result, comp);
				if(sort.getSortType() == SortType.DESCENDING) {
					Collections.reverse(result);
				}
				return result;
			}
		}
		return list;
	}
	
	public static Field getField(Class<?> klass, String expr) {
		String[] fieldNames = expr.split("\\.");
		int k = 0;
		try {
			Field field = null;
			while (k < fieldNames.length) {
				String fieldName = fieldNames[k];
				field = klass.getDeclaredField(fieldName);
				klass = field.getType();
				k++;
			}
			return field;
		} catch (NoSuchFieldException | SecurityException e) {
			return null;
		}
	}
	
	public static <K,V> Map<K, List<V>> setToMap(List<K> list) {
	    Map<K, List<V>> result = new HashMap<>();
	    Set<K> set = new HashSet<>(list);
	    for (K item : set) {
	        result.put(item, new ArrayList<V>());
	    }
	    return result;
	}
	
	public static List<?> toList(List<Map<?, ?>> listMap, Class<?> clazz, String alias) {
        List<Object> listObject = new ArrayList<>();
        for(Map<?, ?> map : listMap){
            listObject.add(clazz.cast( map.get(alias)));
        }
        return listObject;
    }
}
