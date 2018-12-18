package com.microservices.user.core.dao;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;
import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.DaoException;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.SortState.SortType;

@Transactional
public class DAOTemplate<K extends Serializable, T> {
	
	protected static interface DAOUnitOfWork<W> {
		public W run(Session session);
	}
	
	protected Class<T> typeParameterClass;
	
	protected String idField;
	
	protected CriteriaBuilder criteriaBuilder;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public DAOTemplate(Class<T> typeParameterClass) {
	    this.typeParameterClass = typeParameterClass;
	    Field idField = getEntityIdField(typeParameterClass);
	    if (idField == null) {
	        throw DaoException.IdFieldEmpty();
	    }
	    this.idField = idField.getName();
	    this.criteriaBuilder = new CriteriaBuilder(typeParameterClass, this.idField);
	}
	
	public DAOTemplate(Class<T> typeParameterClass, String idField) {
		this.typeParameterClass = typeParameterClass;
		this.idField = idField;
		this.criteriaBuilder = new CriteriaBuilder(typeParameterClass, idField);
	}
	
    public static Field getEntityIdField(Class<?> klass) {
        for(Field field  : klass.getDeclaredFields()) {
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
	
	protected List<T> query(DetachedCriteria criteria) {
		return query(criteria, null);
	}
	
	protected List<T> query(final DetachedCriteria detachedCr, final Integer firstResult, final Integer maxResults) {
	    PagingState paging = new PagingState(firstResult, maxResults);
	    return query(detachedCr, paging);
	}
	
	protected List<T> query(final DetachedCriteria detachedCr, final PagingState paging) {
		DAOUnitOfWork<List<T>> uow = new DAOUnitOfWork<List<T>>() {

			@SuppressWarnings("unchecked")
			@Override
			public List<T> run(Session session) {
				Criteria cr = detachedCr.getExecutableCriteria(session);
				if (paging != null) {
					criteriaBuilder.addPaging(paging, cr);
				}
				return (List<T>) cr.list();
			}
			
		};
		
		return executeUnitOfWork(uow);
	}
	
	
    public List<T> query(final QuerySpecification spec) {
        return executeUnitOfWork(new DAOUnitOfWork<List<T>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<T> run(Session session) {
                DetachedCriteria dcr = createQuery();
                criteriaBuilder.buildCriteria(spec, dcr);
                if (spec.isUseRootDistinct()) {
                    dcr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
                }
                Criteria cr = dcr.getExecutableCriteria(session);
                criteriaBuilder.addPaging(spec.getPaging(), cr);
                return cr.list();
            }
            
        });
    }
    
    public T queryFirst(final QuerySpecification spec) {
        List<T> list = query(spec);
        return (! list.isEmpty()) ? list.get(0) : null;
    }
    
    public <M> List<M> queryIdList(final QuerySpecification spec) {
        return executeUnitOfWork(new DAOUnitOfWork<List<M>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<M> run(Session session) {
                DetachedCriteria dcr = createQuery();
                criteriaBuilder.buildCriteria(spec, dcr);
                criteriaBuilder.buildProjectionListForIdQuery(spec, dcr);
                Criteria cr = dcr.getExecutableCriteria(session);
                criteriaBuilder.addPaging(spec.getPaging(), cr);
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
            
        });
    }
    
    public List<Map<?, ?>> queryMap(final QuerySpecification spec) {
        return executeUnitOfWork(new DAOUnitOfWork<List<Map<?, ?>>>() {

            @SuppressWarnings("unchecked")
            @Override
            public List<Map<?, ?>> run(Session session) {
                DetachedCriteria dcr = createQuery();
                criteriaBuilder.buildCriteria(spec, dcr);
                criteriaBuilder.buildProjectionListForFieldQuery(spec, dcr);
                Criteria cr = dcr.getExecutableCriteria(session);
                criteriaBuilder.addPaging(spec.getPaging(), cr);
                cr.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
                List<Map<?, ?>> list = cr.list();
                return list;
            }
            
        });
    }


    
    public List<T> query(final String hql, final Map<String, Object> params) {
        return query(hql, params, null);
    }
    
    protected <G> List<G> query(final String hql, final Map<String, Object> params, final ResultTransformer transformer) {
        return executeUnitOfWork(new DAOUnitOfWork<List<G>>() {
            
            @SuppressWarnings("unchecked")
            @Override
            public List<G> run(Session session) {
                Query query = session.createQuery(hql);
                setParamsToQuery(query, params);
                if (transformer != null) {
                    query.setResultTransformer(transformer);
                }
                return query.list();
            }
        });
    }
    
    protected T queryFirst(final String hql, final Map<String, Object> params) {
        List<T> list = query(hql, params);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    protected <G> G queryFirst(final String hql, final Map<String, Object> params, ResultTransformer transformer) {
        List<G> list = query(hql, params, transformer);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    protected T queryFirst(DetachedCriteria criteria) {
        List<T> list = query(criteria);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
    protected long getRecordCount(final DetachedCriteria detached) {
        detached.setProjection(Projections.rowCount());
        return executeUnique(detached);
    }
    
    public long getRecordCount(final QuerySpecification spec) {
        return executeUnitOfWork(new DAOUnitOfWork<Long>() {
            @Override
            public Long run(Session session) {
                DetachedCriteria dcr = createQuery();
                criteriaBuilder.buildRecordCountCriteria(spec, dcr);
                Criteria cr = dcr.getExecutableCriteria(session);
                Long result = (Long)cr.uniqueResult();
                return (result != null) ? result : 0;
            }
            
        });
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
		catch(BaseException ex) {
		    throw ex;
		}
		catch(Exception ex) {
			throw DaoException.create(ex);
		}
		finally {
			closeSession(session);
		}
	}
	
	public List<K> getIdList(List<T> list, String idField) {
		return DataGlue.getFiledFromList(list, typeParameterClass, idField);
	}
	
	public List<K> getIdList(List<T> list) {
		if (idField == null) {
			throw DaoException.IdFieldEmpty();
		}
		return getIdList(list, idField);
	}
	
	public static Criterion isActivePredicate(boolean active) {
		return Restrictions.eq("active", active);
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
	
	protected static Criteria addListState(Criteria criteria, QueryState listState) {
		if (listState == null ) {
			return criteria;
		}
		
		PagingState pageState = listState.getPagingState();
		if( pageState == null) {
			return criteria;
		}
		
		criteria =  criteria.setFirstResult(pageState.getCurrentPosition());
		criteria =  criteria.setMaxResults(pageState.getPageSize());
		return criteria;
	}
	
	protected static Query addPaging(Query query, QuerySpecification specification) {
		if(specification != null) {
			PagingState paging = specification.getPaging();
			if (paging != null) {
				query.setFirstResult(paging.getCurrentPosition());
				query.setMaxResults(paging.getPageSize());
			}
		}
		return query;
	}

	public List<T> getAll() {
		DetachedCriteria cr = createQuery();
		cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return query(cr);
	}
	
	public List<T> getAllActive() {
		return query(createQuery().add(isActivePredicate(true)));
	}
	
	public List<T> getByField(String fieldName, Object fieldValue) {		
		DetachedCriteria criteria = createQuery();
		criteria.add(Restrictions.eq(fieldName, fieldValue));
		return query(criteria);
	}

	public T getById(K id) {
		DetachedCriteria cr = createQuery();
		cr.add(Restrictions.idEq(id));
		return queryFirst(cr);
	}
	
	public T getById(K id, QuerySpecification specification) {
        DetachedCriteria cr = createQuery();
        criteriaBuilder.addAliases(specification, cr);
        criteriaBuilder.addFilters(specification, cr);
        cr.add(Restrictions.idEq(id));
        cr.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return queryFirst(cr);
    }
	
	public List<T> getByList(List<K> list, String fieldName) {
		if(list.size() == 0) {
			return new ArrayList<>();
		}
		DetachedCriteria criteria = createQuery();
		criteria.add(Restrictions.in(fieldName, list));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return query(criteria);
	}
	
	public List<T> getByList(List<K> list, String fieldName, QuerySpecification specification) {
        if(list.size() == 0) {
            return new ArrayList<>();
        }
        
        DetachedCriteria criteria = createQuery();
        criteriaBuilder.addSorting(specification, criteria);
        criteriaBuilder.addSorting(criteria, idField, SortType.ASCENDING);
        criteriaBuilder.addAliases(specification, criteria);
        criteria.add(Restrictions.in(fieldName, list));
        criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        return query(criteria);
    }
	
	public List<T> getByIdList(List<K> list) {
		return getByList(list, idField);
	}
	
	public List<T> getByIdList(List<K> list, QuerySpecification specification) {
        return getByList(list, idField,specification);
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
		} catch (Exception ex) {
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
		} catch (Exception ex) {
			closeSession(session);
			throw DaoException.create(ex);
		} 
		closeSession(session);
	}
	
	protected void buildCriteria(
			DetachedCriteria cr, List<Criterion> searchList, List<Criterion> filters) {
		criteriaBuilder.buildCriteria(cr, searchList, filters);
	}

	protected void setParamsToQuery(Query query, Map<String, Object> params) {
	    if (params != null) {
            for(Map.Entry<String, Object> entry : params.entrySet()) {
                if ( entry.getValue() instanceof ArrayList<?>) {
                    ArrayList<?> list = (ArrayList<?>) entry.getValue();
                    query.setParameterList(entry.getKey(), list);
                }
                else {
                    query.setParameter(entry.getKey(), entry.getValue());
                }
            }
        }
	}
	
}
