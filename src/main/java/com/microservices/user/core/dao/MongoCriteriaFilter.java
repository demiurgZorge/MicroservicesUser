package com.microservices.user.core.dao;

import org.springframework.data.mongodb.core.query.Criteria;

import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.QueryFilter;

public abstract class MongoCriteriaFilter implements QueryFilter {
    
    private String name;
    private FieldType fieldType;
    
    public abstract Criteria build(Object value);
    
    public MongoCriteriaFilter(String name, FieldType type) {
        this.name = name;
        this.fieldType = type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public FieldType getFieldType() {
        return fieldType;
    }
    
    public static MongoCriteriaFilter eq(FilterEnum filter, String expression, FieldType type) {
        return eq(filter.toString(), expression, type);
    }
    
    public static MongoCriteriaFilter eq(String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {
            
            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).is(FieldType.convertType(value, type));
            }
        };
    }
    
    public static MongoCriteriaFilter lte(FilterEnum filter, String expression, FieldType type) {
        return lte(filter.toString(), expression, type);
    }
    
    public static MongoCriteriaFilter lte(String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {
            
            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).lte(FieldType.convertType(value, type));
            }
        };
    }
    
    public static MongoCriteriaFilter gte(FilterEnum filter, String expression, FieldType type) {
        return gte(filter.toString(), expression, type);
    }
    
    public static MongoCriteriaFilter gte(String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {
            
            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).gte(FieldType.convertType(value, type));
            }
        };
    }
    
    public static MongoCriteriaFilter in(FilterEnum filter, String expression, FieldType type) {
        return in(filter.toString(), expression, type);
    }
    
    public static MongoCriteriaFilter in(final String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {
            
            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).in(FieldType.convertList(name, value, type));
            }
        };
    }

    public static MongoCriteriaFilter ilkike(FilterEnum filter, String expression, FieldType type) {
        return ilkike(filter.toString(), expression, type);
    }

    public static MongoCriteriaFilter ilkike(String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {

            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).regex(FieldType.convertType(value, type).toString());
            }
        };
    }

    public static MongoCriteriaFilter exists(FilterEnum filter, String expression, FieldType type) {
        return exists(filter.toString(), expression, type);
    }

    public static MongoCriteriaFilter exists(String name, final String expression, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {

            @Override
            public Criteria build(Object value) {
                return Criteria.where(expression).exists((Boolean)FieldType.convertType(value, type));
            }
        };
    }

    public static MongoCriteriaFilter fromCriteria(FilterEnum filter, Criteria cr, FieldType type) {
        return fromCriteria(filter.toString(), cr, type);
    }

    public static MongoCriteriaFilter fromCriteria(String name, final Criteria cr, final FieldType type) {
        return new MongoCriteriaFilter(name, type) {
            
            @Override
            public Criteria build(Object value) {
                return cr;
            }
        };
    }
}
