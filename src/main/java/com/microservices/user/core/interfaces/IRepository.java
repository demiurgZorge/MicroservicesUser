package com.microservices.user.core.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IRepository<T, K extends Serializable > {
    
    T getById(K id, IUnitOfWork uow);
    T tryGetById(K id, IUnitOfWork uow);
    
    List<T> query(final IQuerySpecification spec, IUnitOfWork uow);
    T queryFirst(final IQuerySpecification spec, IUnitOfWork uow);
    
    <M> List<M> queryIdList(final IQuerySpecification spec, IUnitOfWork uow);
    List<Map<?, ?>> queryMap(final IQuerySpecification spec, IUnitOfWork uow);
    
    long getRecordCount(final IQuerySpecification spec, IUnitOfWork uow);
    List<T> getByIdList(List<K> list, IUnitOfWork uow);
    
}
