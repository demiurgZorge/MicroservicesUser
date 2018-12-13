package com.microservices.user.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservices.user.core.exceptions.BaseException;
import com.microservices.user.core.exceptions.DaoException;

public class DAOTemplate<K extends Serializable, T> {
    
    protected static interface DAOUnitOfWork<W> {
        public W run(Session session);
    }
    
    protected Class<T>     typeParameterClass;
    
    protected String       idField;
    
    @Autowired
    private SessionFactory sessionFactory;
    
    public DAOTemplate(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
        Field idField = getEntityIdField(typeParameterClass);
        if (idField == null) {
            throw DaoException.IdFieldEmpty();
        }
        this.idField = idField.getName();
    }
    
    public DAOTemplate(Class<T> typeParameterClass, String idField) {
        this.typeParameterClass = typeParameterClass;
        this.idField = idField;
    }
    
    public static Field getEntityIdField(Class<?> klass) {
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }
    
    protected Session tryOpenSession() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        return session;
    }
    
    protected void closeSession(Session session) {
        if (session != null) {
            session.getTransaction().commit();
            if (session.isOpen()) {
                session.close();
            }
        }
    }
    
    protected DetachedCriteria createQuery() {
        return DetachedCriteria.forClass(typeParameterClass);
    }
    
    protected DetachedCriteria createByIdQuery(K idValue) {
        DetachedCriteria cr = createQuery();
        cr.add(Restrictions.idEq(idValue));
        return cr;
    }
    
    protected DetachedCriteria createQuery(String alias) {
        return DetachedCriteria.forClass(typeParameterClass, alias);
    }
    
    protected Criteria createQuery(Session session) {
        return session.createCriteria(typeParameterClass);
    }
    
    protected List<T> query(final DetachedCriteria detachedCr) {
        DAOUnitOfWork<List<T>> uow = new DAOUnitOfWork<List<T>>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public List<T> run(Session session) {
                Criteria cr = detachedCr.getExecutableCriteria(session);
                return (List<T>) cr.list();
            }
            
        };
        
        return executeUnitOfWork(uow);
    }
    
    protected T queryFirst(DetachedCriteria criteria) {
        List<T> list = query(criteria);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    protected <G> List<G> execute(final DetachedCriteria criteria) {
        DAOUnitOfWork<List<G>> uow = new DAOUnitOfWork<List<G>>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public List<G> run(Session session) {
                Criteria cr = criteria.getExecutableCriteria(session);
                return (List<G>) cr.list();
            }
        };
        
        return executeUnitOfWork(uow);
    }
    
    protected <G> G executeUnique(final DetachedCriteria criteria) {
        DAOUnitOfWork<G> uow = new DAOUnitOfWork<G>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public G run(Session session) {
                Criteria cr = criteria.getExecutableCriteria(session);
                return (G) cr.uniqueResult();
            }
        };
        
        return executeUnitOfWork(uow);
    }
    
    protected <W> W executeUnitOfWork(DAOUnitOfWork<W> uow) {
        Session session = tryOpenSession();
        try {
            W result = uow.run(session);
            return result;
        }
        catch (BaseException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw DaoException.create(ex);
        }
        finally {
            closeSession(session);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void deleteById(K id) {
        Session session = tryOpenSession();
        try {
            T del = (T) session.createCriteria(typeParameterClass).add(Restrictions.idEq(id)).uniqueResult();
            session.delete(del);
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
    public void delete(T obj) {
        Session session = tryOpenSession();
        try {
            session.delete(obj);
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
    public List<T> getAll() {
        DetachedCriteria cr = createQuery();
        cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return query(cr);
    }
    
    public List<T> getByField(String fieldName, Object fieldValue) {
        DetachedCriteria criteria = createQuery();
        criteria.add(Restrictions.eq(fieldName, fieldValue));
        return query(criteria);
    }
    
    public T getById(long i) {
        DetachedCriteria cr = createQuery();
        cr.add(Restrictions.idEq(i));
        cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return queryFirst(cr);
    }
    
    public List<T> getByList(List<K> list, String fieldName) {
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        DetachedCriteria criteria = createQuery();
        criteria.add(Restrictions.in(fieldName, list));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return query(criteria);
    }
    
    public List<T> getByIdList(List<K> list) {
        return getByList(list, idField);
    }
    
    public void update(T object) {
        Session session = null;
        try {
            session = tryOpenSession();
            session.update(object);
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
    public void add(T object) {
        Session session = null;
        try {
            session = tryOpenSession();
            session.save(object);
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
    public void saveOrUpdate(T object) {
        Session session = null;
        try {
            session = tryOpenSession();
            session.saveOrUpdate(object);
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
    public void deleteAll() {
        Session session = null;
        try {
            session = tryOpenSession();
            String hql = String.format("delete from %s", typeParameterClass.getCanonicalName());
            Query query = session.createQuery(hql);
            query.executeUpdate();
        }
        catch (Exception ex) {
            closeSession(session);
            throw DaoException.create(ex);
        }
        closeSession(session);
    }
    
}
