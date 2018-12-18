package com.microservices.user.core.dao;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.FilterState;
import com.microservices.user.core.interfaces.IQuerySpecification;
import com.microservices.user.core.interfaces.PagingState;
import com.microservices.user.core.interfaces.QueryFilter;
import com.microservices.user.core.interfaces.QueryFilter.FieldType;
import com.microservices.user.core.interfaces.SortState;
import com.microservices.user.core.repository.BaseQuerySpecification;

public class QuerySpecification  extends BaseQuerySpecification {
	
    public static enum DefaultFilters implements FilterEnum {
	    search, idList
	}

    protected QueryFilter searchFilter;
    protected QueryFilter idListFilter;
	protected Map<String, QueryFilter> baseFilters;
	protected ViewEnum viewEnum;
	

    public QuerySpecification() {
        super();
        baseFilters = new HashMap<>();
        this.idListFilter = buildIdListFilter();
	}
	
	public QuerySpecification(QueryState query) {
	    this();
        initFromState(query);
    }
	
	@SuppressWarnings("unchecked")
	public <T extends QuerySpecification> T copy() {
	   
        T newSpec = (T)create(this.getClass());
	    
	    newSpec.definedFilters = new HashMap<>(this.definedFilters);
	    newSpec.aliases = new HashMap<>(this.aliases);
	    newSpec.filters = new HashMap<>(this.filters);
	    newSpec.definedSorts = new HashMap<>(this.definedSorts);
	    newSpec.baseFilters = new HashMap<>(this.baseFilters);
	    if (this.paging != null) {
	        newSpec.paging = new PagingState(this.paging.getCurrentPosition(), this.paging.getPageSize());
	    }
	    newSpec.projections = new HashMap<>(this.projections);
	    newSpec.searchFilter = this.searchFilter;
	    if (this.sorting != null) {
	        newSpec.sorting = new SortState(this.sorting.getSortField(), this.sorting.getSortType());
	    }
	    newSpec.useRootDistinct = this.useRootDistinct;
	    return newSpec;
	}
	
	public QueryState buildQueryState() {
		QueryState state = new QueryState();
		
		for (String filterName : this.filters.keySet()) {
			state.addFilter(filterName, this.filters.get(filterName).getFieldValue());
		}
		
		if (this.paging != null) {
			state.setPagingState(new PagingState(this.paging.getCurrentPosition(), this.paging.getPageSize()));
	    }
		
		if (this.sorting != null) {
			state.setSorting(new SortState(this.sorting.getSortField(), this.sorting.getSortType()));
	    }
		
		return state;
	}
	
    protected void initFromState(QueryState state) {
		if (state == null) {
			state = new QueryState();
		}
		paging = state.getPagingState();
		initFilters(state.getFilters());
		this.searchFilter = buildSearchFilter(state);
		initSorting(state.getSorting());
		if (state.getQueryString() != null && searchFilter != null) {
			setFilterValue(searchFilter.getName(), state.getQueryString());
		}
	}

    protected void initFilters(List<FilterState> filters) {
		for(FilterState filterValue : filters ) {
			setFilterValue(filterValue);
		}
	}
	
	//override this
	protected QueryFilter buildSearchFilter(QueryState state) {
	    return EmptyQueryFilter.create(QuerySpecification.DefaultFilters.search);
	}
	
	protected QueryFilter buildIdListFilter() {
	    return EmptyQueryFilter.create(QuerySpecification.DefaultFilters.idList);
	}
	
	protected QueryFilter initSearchFilter(QueryState state) {
	    QueryFilter filter = EmptyQueryFilter.create(QuerySpecification.DefaultFilters.search);
	    if (state != null && state.getQueryString() != null && !state.getQueryString().isEmpty()) {
	        filter = buildSearchFilter(state);
	        setSearchFilter(filter);
	    }
        setSearchFilter(filter);
        return filter;
	}
	
	public QuerySpecification addBaseFilter(QueryFilter filter, Object value) {
		String name = filter.getName();
		baseFilters.put(name, filter);
		filters.put(name, new FilterState(name, value));
		return this;
	}
	
	
	
	public QueryFilter getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(QueryFilter searchFilter) {
		if (this.searchFilter != null) {
			removeDefinedFilter(this.searchFilter.getName());
		}
		
		this.searchFilter = searchFilter;
		if (searchFilter != null) {
			defineFilter(searchFilter);
		}
	}
	
	@Override
	public QueryFilter getDefinedFilter(String name) {
	    QueryFilter qf = super.getDefinedFilter(name);
	    if (qf != null) {
	        return qf;
	    }
	    return baseFilters.get(name);
	}
   
    @SuppressWarnings("unchecked")
    public static <T>  T create(Class<T> klass) {
        try {
            IQuerySpecification newSpec = (IQuerySpecification)klass.newInstance();
            return (T)newSpec;
        }
        catch (InstantiationException | IllegalAccessException e) {
            throw new BaseException(e);
        }
    }
    
    public static <T extends IQuerySpecification> T create(QueryState qs, Class<T> klass) {
    	try {
    		Class<?>[] cArgTypes = new Class[] {QueryState.class};
            Constructor<T> newSpec = klass.getDeclaredConstructor(cArgTypes);
            Object[] args = new Object[] {qs};
            return newSpec.newInstance(args);
        }
        catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new BaseException(e);
        }
    }
    
    public static <T extends QuerySpecification> T createQueryByIdList(List<Long> list,  Class<T> klass) {
        T spec = create(klass);
        if (spec.idListFilter.getFieldType() == FieldType.INTEGER) {
            spec.setFilterValue(spec.idListFilter.getName(), DataGlue.listCast(list));
        }
        else {
            spec.setFilterValue(spec.idListFilter.getName(), list);
        }
        return spec;
    }

	public ViewEnum getViewEnum() {
		return viewEnum;
	}

	public void setViewEnum(ViewEnum viewEnum) {
		this.viewEnum = viewEnum;
	}
}
