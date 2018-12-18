package com.microservices.user.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import com.microservices.user.core.interfaces.FilterState;
import com.microservices.user.core.interfaces.IHibernateQueryBuilder;
import com.microservices.user.core.interfaces.IQuerySpecification;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.QueryFilter;
import com.microservices.user.core.interfaces.QuerySpecificationAlias;
import com.microservices.user.core.interfaces.SortSpecification;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.interfaces.SortState.SortType;


public class CriteriaBuilder implements IHibernateQueryBuilder {
    
    protected Class<?> klass;
    
    protected String idField;
    
    public CriteriaBuilder(Class<?> klass, String idField) {
        this.klass = klass;
        this.idField = idField;
    }
    
    public static void addSearchByNumberField(List<Criterion> searchList, String searchString, String fieldName) {
        try {
            Long id = Long.parseLong(searchString);
            searchList.add(Restrictions.eq(fieldName, id));
        }
        catch (Exception e) {
            // nothing do here
        }
    }

    public static void addLikeCriterion(Junction cr, String fieldName, String searchString) {
        if (searchString != null && !searchString.isEmpty()) {
            cr.add(Restrictions.ilike(fieldName, searchString, MatchMode.ANYWHERE));
        }
    }

    public static <G> void addListCriterion(Junction cr, String fieldName, List<G> items) {
        if (items != null && items.size() > 0) {
            cr.add(Restrictions.in(fieldName, items));
        }
    }

    public static void addSearchByString(List<Criterion> searchList, List<String> fieldsList, String searchString) {
        if (searchString != null && !searchString.isEmpty()) {
            for (String fieldName : fieldsList) {
                searchList.add(Restrictions.ilike(fieldName, searchString, MatchMode.ANYWHERE));
            }
        }
    }

    public static Disjunction or(List<Criterion> list) {
        Criterion[] arr = new Criterion[list.size()];
        Disjunction or = Restrictions.or(list.toArray(arr));
        return or;
    }

    protected void buildCriteria(DetachedCriteria cr, List<Criterion> searchList, List<Criterion> filters) {

        Conjunction and = Restrictions.and();

        boolean needAppendCriterions = false;
        if (searchList != null && searchList.size() > 0) {
            and.add(or(searchList));
            needAppendCriterions = true;
        }

        if (filters != null && filters.size() > 0) {
            Criterion[] arr = new Criterion[filters.size()];
            Conjunction filterCr = Restrictions.and(filters.toArray(arr));
            and.add(filterCr);
            needAppendCriterions = true;
        }

        if (needAppendCriterions) {
            cr.add(and);
        }
    }
    
    public void buildRecordCountCriteria(IQuerySpecification spec, DetachedCriteria cr) {
        addAliases(spec, cr);
        Criterion filters = buildFilters(spec);
        if (filters != null) {
            cr.add(filters);
        }
        
        ProjectionList prl = Projections.projectionList();
        if (spec.isUseRootDistinct()) {
            prl.add(Projections.countDistinct(idField));
        }
        else {
            prl.add(Projections.count(idField));
        }
        cr.setProjection(prl);
    }
    
    public void buildCriteria(IQuerySpecification spec, DetachedCriteria cr) {
        addAliases(spec, cr);
        addFilters(spec, cr);
        addSorting(spec, cr);
    }

    protected void addFilters(IQuerySpecification spec, DetachedCriteria cr) {
        Criterion filters = buildFilters(spec);
        if (filters != null) {
            cr.add(filters);
        }
    }

    protected void addAliases(IQuerySpecification spec, DetachedCriteria cr) {
        for (String key : spec.getAliases()) {
            QuerySpecificationAlias alias = spec.getAlias(key);
            if (alias.joinType == QuerySpecificationAlias.JoinType.INNER) {
                cr.createAlias(alias.path, alias.name, JoinType.INNER_JOIN);
            }
            else {
                cr.createAlias(alias.path, alias.name, JoinType.LEFT_OUTER_JOIN);
                cr.setFetchMode(alias.name, FetchMode.JOIN);
            }
        }
    }

    protected Criterion buildFilters(IQuerySpecification spec) {
        if (spec == null) {
            return null;
        }

        List<FilterState> filterValues = spec.getFilterValues();
        List<Criterion> criterionList = new ArrayList<>();

        for (FilterState fs : filterValues) {
            QueryFilter filter = spec.getDefinedFilter(fs.getFilterName());
            if (filter != null && filter instanceof HibernateCriteriaQueryFilter) {
                criterionList.add(((HibernateCriteriaQueryFilter) filter).build(fs.getFieldValue()));
            }
        }

        if (criterionList.size() == 0) {
            return null;
        }
        else if (criterionList.size() == 1) {
            return criterionList.get(0);
        }
        else {
            Conjunction and = Restrictions.and();
            for (Criterion cr : criterionList) {
                and.add(cr);
            }
            return and;
        }
    }

    public void addPaging(PagingState paging, Criteria cr) {
        if (paging != null) {
            cr.setFirstResult(paging.getCurrentPosition()).setMaxResults(paging.getPageSize());
        }
    }
    

    public void addSorting(IQuerySpecification spec, DetachedCriteria criteria) {
        SortState sortState = spec.getSorting();
        SortSpecification<?> sorting = spec.getSortSpecification();
        if (sortState == null || sorting == null || !(sorting instanceof HibCriteriaSortSpecification<?>)) {
            addSorting(criteria, this.idField, SortType.ASCENDING);
            return;
        }
        
        HibCriteriaSortSpecification<?> hibSorting = (HibCriteriaSortSpecification<?>) sorting;
        addSorting(criteria, hibSorting.getExpression(), sortState.getSortType());
        if (! sorting.getExpression().equals(this.idField) ) {
            addSorting(criteria, this.idField, SortType.ASCENDING);
        }
    }
    
    protected void addSorting(DetachedCriteria criteria, String expression, SortType type ) {
        if (type == SortType.ASCENDING) {
            criteria.addOrder(Order.asc(expression));
        }
        else {
            criteria.addOrder(Order.desc(expression));
        }
    }
    
    protected void addSortToProjectionList(IQuerySpecification spec, ProjectionList prl) {
        SortState sortState = spec.getSorting();
        SortSpecification<?> sorting = spec.getSortSpecification();
        if (sortState == null || sorting == null || !(sorting instanceof HibCriteriaSortSpecification<?>)) {
            return;
        }
        
        HibCriteriaSortSpecification<?> hibSorting = (HibCriteriaSortSpecification<?>) sorting;
        prl.add(Projections.property(hibSorting.getExpression()));
    }
    
    public void buildProjectionListForIdQuery(IQuerySpecification spec, DetachedCriteria dcr) {
        ProjectionList prl = Projections.projectionList();
        addSortToProjectionList(spec, prl);
        prl.add(Projections.id());
        dcr.setProjection(Projections.distinct(prl));
        
    }
    
    public void buildProjectionListForFieldQuery(IQuerySpecification spec, DetachedCriteria dcr) {
        if (spec.getProjectionFields().size() > 0) {
            ProjectionList prl = Projections.projectionList();
            Set<String> aliases = spec.getProjectionFields();
            for (String alias : aliases) {
                prl.add(Projections.property(spec.getProjectionField(alias)), alias);
            }
            if (spec.isUseRootDistinct()) {
                dcr.setProjection(Projections.distinct(prl));
            }
        }
    }
}
