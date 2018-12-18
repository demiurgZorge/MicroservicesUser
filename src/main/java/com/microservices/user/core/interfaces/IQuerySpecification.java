package com.microservices.user.core.interfaces;

import java.util.List;
import java.util.Set;

public interface IQuerySpecification {
    
    boolean isUseRootDistinct();
    List<String> getAliases();
    QuerySpecificationAlias getAlias(String name);
    QueryFilter getDefinedFilter(String name);
    List<FilterState> getFilterValues();
    SortState getSorting();
    SortSpecification<?> getSortSpecification();
    PagingState getPaging();
    Set<String> getProjectionFields();
    String getProjectionField(String alias);
    
}
