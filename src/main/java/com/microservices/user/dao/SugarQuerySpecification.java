package com.microservices.user.dao;

import com.microservices.user.core.dao.HibCriteriaSortSpecification;
import com.microservices.user.core.dao.HibernateCriteriaQueryFilter;
import com.microservices.user.core.dao.QuerySpecification;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.QueryFilter.FieldType;
import com.microservices.user.core.interfaces.QuerySpecificationAlias.JoinType;
import com.microservices.user.core.interfaces.SortEnum;
import com.microservices.user.db.models.Sugar;

public class SugarQuerySpecification extends QuerySpecification {
    public static enum Filters implements FilterEnum {
        idList, patientId, maxDatetime, minDatetime, maxLevel, minLevel
    }
    
    public enum SearchSortTypes implements SortEnum {
        byDatetime
    }
    
    public SugarQuerySpecification(QueryState state){
        super();
        defineAlias("patient", "patient", JoinType.LEFT);
        useRootDistinct = true;
        defineFilter(getByIdListFilter());
        defineFilter(getByPatientIdFilter());
        
        defineFilter(getMaxLevelFilter());
        defineFilter(getMinLevelFilter());
        
        defineFilter(getMaxDatetimeFilter());
        defineFilter(getMinDatetimeFilter());
        defineSorting(getByDatetimeSort());
        initFromState(state);
    }
    
    private static HibernateCriteriaQueryFilter getByIdListFilter() {
        return HibernateCriteriaQueryFilter.in(Filters.idList, "id", FieldType.LONG);
    }
    
    private static HibernateCriteriaQueryFilter getByPatientIdFilter() {
        return HibernateCriteriaQueryFilter.eq(Filters.patientId, "patient.id", FieldType.LONG);
    }
    
    private static HibernateCriteriaQueryFilter getMaxLevelFilter() {
        return HibernateCriteriaQueryFilter.le(Filters.maxLevel, "level", FieldType.FLOAT);
    }

    private static HibernateCriteriaQueryFilter getMinLevelFilter() {
        return HibernateCriteriaQueryFilter.ge(Filters.minLevel, "level", FieldType.FLOAT);
    }
    
    private static HibernateCriteriaQueryFilter getMaxDatetimeFilter() {
        return HibernateCriteriaQueryFilter.le(Filters.maxDatetime, "datetime", FieldType.DATE);
    }

    private static HibernateCriteriaQueryFilter getMinDatetimeFilter() {
        return HibernateCriteriaQueryFilter.ge(Filters.minDatetime, "datetime", FieldType.DATE);
    }
    
    private static HibCriteriaSortSpecification<Sugar> getByDatetimeSort() {
        return HibCriteriaSortSpecification.create(SearchSortTypes.byDatetime, "datetime");
    }
}
