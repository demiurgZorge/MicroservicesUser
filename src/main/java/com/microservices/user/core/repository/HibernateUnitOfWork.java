package com.microservices.user.core.repository;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;
import com.microservices.user.core.interfaces.IUnitOfWork;
import com.microservices.user.core.interfaces.RepositoryException;

@Component("HibernateUnitOfWork")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
public class HibernateUnitOfWork implements IUnitOfWork {
    
    
    private static final Logger  logger = LoggerFactory.getLogger(HibernateUnitOfWork.class);
    
    public enum Errors implements ErrorCodeEnum {
        UNKNOWN_UOW_ERROR("unknown dao error got his way to life"),
        UNIT_OF_WORK_ALREADY_COMPLETE("Unit of work already complete");
        
        private String message; 
        
        private Errors(String message) {
            this.message = message;
        }
        
        @Override
        public String code() {
            return this.name();
        }
        
        @Override
        public String toString() {
            return this.message;
        }
    }
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session session;

    private Transaction transaction;
    
    private boolean active;
    
    @PostConstruct
    private void init() {
        this.session = sessionFactory.openSession();
        this.transaction = session.beginTransaction();
        this.active = true;
    }
    
    @Override
    public boolean contains(Object object) {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        return session.contains(object);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void evict(Object object) {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        session.evict(object);
    }

    @Override
    public void persist(Object object) {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        session.persist(object);        
    }

    @Override
    public void delete(Object object) {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        session.delete(object);
    }

    @Override
    public void commit() {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        transaction.commit();
        session.close();
        this.active = false;
    }

    @Override
    public void rollback() {
        if (!active) {
            throw RepositoryException.create(logger, Errors.UNIT_OF_WORK_ALREADY_COMPLETE);
        }
        transaction.rollback();
        session.close();
        this.active = false;
    }
    
    public Session getSession() {
        return session;
    }
}
