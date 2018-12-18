package com.microservices.user.core.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.microservices.user.core.interfaces.FilterState;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.QueryFilter;
import com.microservices.user.core.interfaces.SortSpecification;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.interfaces.SortState.SortType;

public class MongoQueryBuilder {
    
    protected void addPaging(Query query, PagingState paging) {
        if (paging != null) {
            query.skip(paging.getCurrentPosition());
            query.limit(paging.getPageSize());
        }
    }
    
    protected void addSorting(QuerySpecification spec, Query query) {
        SortState sortState = spec.getSorting();
        if (sortState == null) {
            return;
        }
        SortSpecification<?> sorting = spec.getSortSpecification(sortState.getSortField());
        if (sorting == null || !(sorting instanceof MongoSortSpecification<?>)) {
            return;
        }
        addSorting(query, sorting.getExpression(), sortState.getSortType()); 
    }
    
    protected void addSorting(Query query, String expression, SortType type ) {
        if (type == SortType.ASCENDING) {
            query.with(new Sort(Sort.Direction.ASC, expression));
        }
        else {
            query.with(new Sort(Sort.Direction.DESC, expression));
        }
    }
    
    protected void addFilter(Query query, MongoCriteriaFilter filter, FilterState state) {
        if (state == null) {
            return;
        }
        Object value = state.getFieldValue();
        Criteria cr = filter.build(value);
        query.addCriteria(cr);
    }
    
    protected void addFilters(QuerySpecification spec, Query query) {
        List<FilterState> filterValues = spec.getFilterValues();
        List<Criteria> criteriaList = new ArrayList<>();
        
        for (FilterState fs : filterValues) {
            QueryFilter filter = spec.getFilterDefinition(fs, MongoCriteriaFilter.class);
            if (filter != null) {
                criteriaList.add(((MongoCriteriaFilter) filter).build(fs.getFieldValue()));
            }
        }
    
        for (Criteria cr : criteriaList) {
            query.addCriteria(cr);
        }
    }
    
    protected void buildCriteria(QuerySpecification spec, Query query) {
        if (spec == null) {
            return;
        }
        
        addFilters(spec, query);
        addSorting(spec, query);
        addPaging(query, spec.getPaging());
        
        return;
    }
}
