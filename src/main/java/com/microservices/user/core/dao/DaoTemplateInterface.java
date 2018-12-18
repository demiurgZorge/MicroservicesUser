package com.microservices.user.core.dao;

import java.io.Serializable;
import java.util.List;

public interface DaoTemplateInterface<K extends Serializable, T> {

	List<K> getIdList(List<T> list, String idField);
	
	List<K> getIdList(List<T> list);
	
	void deleteById(K id);

	void delete(T obj);

	List<T> getAll();
	
	List<T> query(final QuerySpecification spec);
	
	<M> List<M> queryIdList(final QuerySpecification spec);
	
	long getRecordCount(final QuerySpecification spec);
	
	List<T> getByField(String fieldName, Object fieldValue);

	T getById(K id);

	List<T> getByList(List<K> list, String fieldName);
	
	List<T> getByIdList(List<K> list);
	
	List<T> getByIdList(List<K> idList, QuerySpecification spec);
	
	void update(T object);

	void add(T object);

	void deleteAll();

	void saveOrUpdate(T object);
	
}