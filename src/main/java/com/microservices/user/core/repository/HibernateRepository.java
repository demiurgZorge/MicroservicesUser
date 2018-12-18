package com.microservices.user.core.repository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microservices.user.core.dao.CriteriaBuilder;
import com.microservices.user.core.dao.exceptions.DaoException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;
import com.microservices.user.core.interfaces.IQuerySpecification;
import com.microservices.user.core.interfaces.IRepository;
import com.microservices.user.core.interfaces.IUnitOfWork;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.RepositoryException;


public class HibernateRepository<T, K extends Serializable> implements IRepository<T, K> {
    
    private static final Logger  logger = LoggerFactory.getLogger(HibernateRepository.class);
    
    protected Class<T> typeParameterClass;
    
    protected String idField;

    protected CriteriaBuilder queryBuilder;
    
    
    
    public enum Errors implements ErrorCodeEnum {
        UNKNOWN_REPOSITORY_ERROR("unknown dao error got his way to life"),
        SESSION_IS_CLOSED("Session is closed"),
        ID_FIELD_NOT_SUPPLIED("Id field not supplied to DAO constructor"),
        OBJECT_NOT_FOUND_BY_ID("Object of class %s and id %s not found"),
        SESSION_IS_NULL("Session is null"), 
        SPECIFICATION_RETURN_INVALID_QUERY_BUILDER("Specification return invalid query bulder");
        
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
    
    public HibernateRepository(Class<T> typeParameterClass) {
        this.typeParameterClass = typeParameterClass;
        Field idField = getEntityIdField(typeParameterClass);
        if (idField == null) {
            throw DaoException.IdFieldEmpty();
        }
        this.idField = idField.getName();
        this.queryBuilder = new CriteriaBuilder(typeParameterClass, this.idField);
    }
    
    @Override
    public T getById(K id, IUnitOfWork uow) {
        DetachedCriteria cr = createQuery();
        cr.add(Restrictions.idEq(id));
        return queryFirst(cr, uow);
    }
    
    @Override
    public T tryGetById(K id, IUnitOfWork uow) {
        T t = getById(id, uow);
        if (t == null) {
            throw RepositoryException.create(logger, Errors.OBJECT_NOT_FOUND_BY_ID, typeParameterClass.getSimpleName(), id);
        }
        return t;
    }
    
    @Override
    public List<T> query(IQuerySpecification spec, IUnitOfWork uow) {
        DetachedCriteria dcr = createQuery();
        queryBuilder.buildCriteria(spec, dcr);
        if (spec.isUseRootDistinct()) {
            dcr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        }
        return query(dcr, spec.getPaging(), uow);        
    }

    @Override
    public T queryFirst(IQuerySpecification spec, IUnitOfWork uow) {
        List<T> list = query(spec, uow);
        return (! list.isEmpty()) ? list.get(0) : null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> List<M> queryIdList(IQuerySpecification spec, IUnitOfWork uow) {
        Session session = getSession(uow);
        DetachedCriteria dcr = createQuery();
        queryBuilder.buildCriteria(spec, dcr);
        queryBuilder.buildProjectionListForIdQuery(spec, dcr);
        Criteria cr = dcr.getExecutableCriteria(session);
        queryBuilder.addPaging(spec.getPaging(), cr);
        List<Object> list = cr.list();
        if(list.size() == 0) {
            return new ArrayList<>();
        }
        if (list.get(0).getClass().isArray()) {
            List<M> result = new ArrayList<>();
            for(Object o : list) {
                Object[] row = ((Object[])o);
                M id= (M)row[ row.length -1 ];
                result.add(id);
            }
            return result;
        }
        return (List<M>)list;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Map<?, ?>> queryMap(IQuerySpecification spec, IUnitOfWork uow) {
        Session session = getSession(uow);
        DetachedCriteria dcr = createQuery();
        queryBuilder.buildCriteria(spec, dcr);
        queryBuilder.buildProjectionListForFieldQuery(spec, dcr);
        Criteria cr = dcr.getExecutableCriteria(session);
        queryBuilder.addPaging(spec.getPaging(), cr);
        cr.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<?, ?>> list = cr.list();
        return list;
    }

    @Override
    public long getRecordCount(IQuerySpecification spec, IUnitOfWork uow) {
        Session session = getSession(uow);
        DetachedCriteria dcr = createQuery();
        queryBuilder.buildRecordCountCriteria(spec, dcr);
        Criteria cr = dcr.getExecutableCriteria(session);
        Long result = (Long)cr.uniqueResult();
        return (result != null) ? result : 0;
    }

    @Override
    public List<T> getByIdList(List<K> list, IUnitOfWork uow) {
        if(list.size() == 0) {
            return new ArrayList<>();
        }
        DetachedCriteria criteria = createQuery();
        criteria.add(Restrictions.in(idField, list));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return query(criteria, uow);
    }

    protected DetachedCriteria createQuery() {
        return DetachedCriteria.forClass(typeParameterClass);
    }
    
    protected T queryFirst(DetachedCriteria criteria, IUnitOfWork uow) {
        List<T> list = query(criteria, uow);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    protected List<T> query(DetachedCriteria criteria, IUnitOfWork uow) {
        return query(criteria, null, uow);
    }
    
    @SuppressWarnings("unchecked")
    protected List<T> query(final DetachedCriteria detachedCr, final PagingState paging, IUnitOfWork uow) {
        Session session = getSession(uow);
        Criteria cr = detachedCr.getExecutableCriteria(session);
        if (paging != null) {
            queryBuilder.addPaging(paging, cr);
        }
        return (List<T>)cr.list();
    }
    
    private Session getSession(IUnitOfWork uow) {
        return ((HibernateUnitOfWork)uow).getSession();
    }
    
//    private Session getSession() {
//        //Session ses = sessionFactory.getCurrentSession();
//        Session ses = uowFactory.getSessionFactory().getCurrentSession();
//        if (ses == null) {
//            throw RepositoryException.create(logger,  Errors.SESSION_IS_NULL);
//        }
//        if ( !ses.isConnected() || !ses.isOpen()) {
//            throw RepositoryException.create(logger, Errors.SESSION_IS_CLOSED);
//        }
//        return ses;
//    }
    
    public static Field getEntityIdField(Class<?> klass) {
    	List<Field> fieldList = new ArrayList<Field>();
    	fieldList.addAll(Arrays.asList(klass.getDeclaredFields()));    	
    	
    	if (klass.getSuperclass().isAnnotationPresent(MappedSuperclass.class)) {
    		fieldList.addAll(Arrays.asList(klass.getSuperclass().getDeclaredFields()));
    	}
    	
        for(Field field  : fieldList) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        return null;
    }
    
}
