package com.microservices.user.core.interfaces;

public interface IUnitOfWork {
    
    boolean contains(Object object);
    boolean isActive();
    void evict(Object object);
    void persist(Object object);
    void delete(Object object);
    void commit();
    void rollback();
    
}
