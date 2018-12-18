package com.microservices.user.core.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.FilterState;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.SortEnum;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.interfaces.SortState.SortType;

/**
 * Состояние запроса
 */
public class QueryState {




    /**
     * Строка запроса
     */
	private String queryString;
	
    /**
     * пагинация
     * {@link com.microservices.user.core.dao.PagingState}
     */
	@JsonProperty("paging")
    private PagingState paging;
    /**
     * Состояние сортировки
     * * {@link com.microservices.user.core.dao.SortState}
     */
    private SortState sorting;
    /**
     * Список фильтров
     * * {@link com.microservices.user.core.dao.FilterState}
     */
    private List<FilterState> filters;

    private String view;

    public static QueryState create(Integer currentPosition, Integer pageSize){
        QueryState list = new QueryState();
        list.paging = new PagingState();
        list.paging.setCurrentPosition(currentPosition);
        list.paging.setPageSize(pageSize);
        list.filters = new ArrayList<FilterState>();
        return list;
    }
    

    public QueryState(PagingState pagingState, SortState sortState, List<FilterState> filters) {
        this.paging = pagingState;
        this.sorting = sortState;
        this.filters = filters != null ? 
        		new ArrayList<FilterState>(filters) : 
        		new ArrayList<FilterState>() ;
    }
    
    public QueryState(PagingState pagingState, SortState sortState) {
        this(pagingState, sortState, null);
    }
    
    public QueryState() {
        paging = new PagingState();
        filters = new ArrayList<FilterState>();
    }

    public Object getFilterData(String filterName){
        for(FilterState filterState : this.filters){
            if(filterName.equals(filterState.getFilterName())) {
                return filterState.getFieldValue();
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getFilterData(FilterEnum filter) {
        Object o = getFilterData(filter.toString());
        return (o != null) ? (T)o : null;
    }

    public PagingState getPagingState() {
        return paging;
    }
    
    @JsonIgnore
    public int getPageSize() {
        if (paging == null) {
            return 0;
        }
        return paging.getPageSize();
    }
    
    public void setPagingState(PagingState pagingState) {
        this.paging = pagingState;
    }

    public SortState getSorting() {
        return sorting;
    }

    public void setSorting(SortState sortState) {
        this.sorting = sortState;
    }
    
    public void setSorting(SortEnum name, SortType type) {
        this.sorting = SortState.create(name, type);
    }
    
    public void setSortState(String name, SortType type) {
    	this.sorting = new SortState(name, type);
    }
    
    public List<FilterState> getFilters() {
        return Collections.unmodifiableList(filters);
    }
    
    public FilterState addFilter(String name, Object value) {
    	if(name != null && value != null ) {
    		FilterState filterState = new FilterState(name, value);
    		filters.add(filterState);
    		return filterState;
    	}
    	return null;
    }
    
    public FilterState addFilter(FilterEnum filter, Object value) {
    	return addFilter(filter.toString(), value);
    }
    
    public void setFiltersState(List<FilterState> filters) {
        this.filters = new ArrayList<FilterState>(filters);
    }


	public String getQueryString() {
		return queryString;
	}


	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }
}
