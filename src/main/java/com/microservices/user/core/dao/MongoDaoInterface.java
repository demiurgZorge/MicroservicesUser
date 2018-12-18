package com.microservices.user.core.dao;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

public interface MongoDaoInterface<K, T> {
    public List<T> query(Query query);
    public List<T> query(QuerySpecification spec);
    long getRecordCount(final QuerySpecification spec);
    void saveOrUpdate(T object);
    public Long getNextSequenceId();
}
