package com.microservices.user.core.interfaces;

import org.hibernate.criterion.DetachedCriteria;

public interface IHibernateQueryBuilder extends IQueryBuilder {
    void buildCriteria(IQuerySpecification spec, DetachedCriteria cr);
}
