package com.microservices.user.core.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.FilterState;
import com.microservices.user.core.interfaces.IQuerySpecification;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.Projection;
import com.microservices.user.core.interfaces.QueryFilter;
import com.microservices.user.core.interfaces.QuerySpecificationAlias;
import com.microservices.user.core.interfaces.QuerySpecificationAlias.JoinType;
import com.microservices.user.core.interfaces.SortEnum;
import com.microservices.user.core.interfaces.SortSpecification;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.interfaces.SortState.SortType;

public class BaseQuerySpecification implements IQuerySpecification {

    protected Map<String, FilterState> filters;
    protected Map<String, QueryFilter> definedFilters;
    protected PagingState paging;
    protected SortState sorting;
    protected SortSpecification<?> sortSpecification;
    protected Map<String, SortSpecification<?>> definedSorts;
    protected Map<String, QuerySpecificationAlias> aliases;
    protected boolean useRootDistinct = false;
    protected Map<String, Projection> projections;
    
    
    public BaseQuerySpecification() {
        filters = new HashMap<>();
        aliases = new HashMap<>();
        paging = new PagingState();
        projections = new HashMap<>();
        definedFilters = new HashMap<String, QueryFilter>();
        definedSorts = new HashMap<>();
    }
    
    @Override
    public boolean isUseRootDistinct() {
        return useRootDistinct;
    }

    @Override
    public List<String> getAliases() {
        return new ArrayList<>(aliases.keySet());
    }
    
    @Override
    public QuerySpecificationAlias getAlias(String name) {
        return aliases.get(name);
    }

    @Override
    public QueryFilter getDefinedFilter(String name) {
        return definedFilters.get(name);
    }
    
    public BaseQuerySpecification defineFilter(QueryFilter filter) {
        definedFilters.put(filter.getName(), filter);
        return this;
    }
    
    public BaseQuerySpecification removeDefinedFilter(String name) {
        definedFilters.remove(name);
        return this;
    }
    
    public Projection addProjection(String alias, Class<?> fieldClass, String field){
        Projection proj = new Projection(alias, fieldClass, field);
        projections.put(alias, proj);
        return proj;
    }
    
    public Class<?> getProjectionClass(String alias){
        return projections.get(alias).clazz;
    }
    
    public void setFilterValue(FilterState filterValue) {
        String name = filterValue.getFilterName();
        QueryFilter filter = definedFilters.get(name);
        if (filter != null) {
            filters.put(name, filterValue);
        }
    }
    
    public void setFilterValue(String name, Object value) {
        FilterState filterValue = new FilterState(name, value);
        setFilterValue(filterValue);
    }
    
    public void setFilterValue(FilterEnum filter, Object value) {
        FilterState filterValue = new FilterState(filter.toString(), value);
        setFilterValue(filterValue);
    }
    
    public void resetFilterValue(FilterEnum filter) {
        String name = filter.toString();
        filters.remove(name);
    }
    
    public void defineSorting(SortSpecification<?> sorting) {
        definedSorts.put(sorting.getName(), sorting);
    }
    
    public void setSorting(SortEnum sort, SortType type) {
        SortState sortState = SortState.create(sort, type);
        initSorting(sortState);
    }

    @Override
    public SortState getSorting() {
        return sorting;
    }

    @Override
    public SortSpecification<?> getSortSpecification() {
        return sortSpecification;
    }

    @Override
    public PagingState getPaging() {
        return paging;
    }

    @Override
    public Set<String> getProjectionFields() {
        return projections.keySet();
    }

    @Override
    public String getProjectionField(String alias) {
        return projections.get(alias).field;
    }
    
    
    @SuppressWarnings("unchecked")
    public <Q> Q getFilterDefinition(String name, Class<Q> clazz) {
        QueryFilter qf = getDefinedFilter(name);
        if (qf!=null && clazz.isAssignableFrom(qf.getClass())) {
            return (Q)qf;           
        }
        return null;
    }
    
    public <Q> Q getFilterDefinition(FilterState filterState, Class<Q> clazz) {
        return getFilterDefinition(filterState.getFilterName(), clazz);
    }
    
    @SuppressWarnings("unchecked")
    public <K> K getFilterValue(FilterEnum filterName) {
        return (K)getFilterValue(filterName.toString());
    }
    
    public Object getFilterValue(String name) {
        FilterState filter = filters.get(name);
        if(filter == null) {
            return null;
        }
        return filter.getFieldValue();
    }
    
    public List<FilterState> getFilterValues() {
        return new ArrayList<>(filters.values());
    }
    
    public boolean filterIsActive(FilterEnum filter) {
        return filterIsActive(filter.toString());
    }
    
    public boolean filterIsActive(String filterName) {
        FilterState filter = filters.get(filterName);
        return (filter != null);
    }

    public void setPaging(PagingState paging) {
        this.paging = paging;
    }

    
    public SortSpecification<?> getSortSpecification(String name) {
        return definedSorts.get(name);
    }
    
    public BaseQuerySpecification defineAlias(String property, String aliasName) {
        QuerySpecificationAlias alias = new QuerySpecificationAlias(property, aliasName);
        aliases.put(aliasName, alias);
        return this;
    }
    
    public BaseQuerySpecification defineAlias(String property, String aliasName, JoinType joinType) {
        QuerySpecificationAlias alias = new QuerySpecificationAlias(property, aliasName);
        alias.joinType = joinType;
        aliases.put(aliasName, alias);
        return this;
    }
    
    protected void initSorting(SortState sortState) {
        if(sortState!=null) {
            SortSpecification<?> sortSpec = definedSorts.get(sortState.getSortField());
            if (sortSpec != null) {
                sorting = sortState;
                sortSpecification = sortSpec;
            }
        }
    }
}
