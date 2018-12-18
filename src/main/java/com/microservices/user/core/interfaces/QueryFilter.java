package com.microservices.user.core.interfaces;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microservices.user.core.dao.exceptions.DetailedException;
import com.microservices.user.core.dao.exceptions.QuerySpecErrors;


public interface QueryFilter {
    
	public String getName();
	
	public FieldType getFieldType();
	
	public static enum FieldKind {
	    INTEGER,
        LONG,
        FLOAT,
        DOUBLE,
        BIGDECIMAL,
        STRING,
        DATE,
        BOOL,
        ENUM,
        ENTITY;
	}
	
	public static class FieldType {
		
	    private static final Logger logger = LoggerFactory.getLogger(QueryFilter.class);
	    
	    private final FieldKind fieldKind;
		
	    private final Class<?> customClass;
	    
	    public FieldType(FieldKind typeEnum, Class<?> customClass) {
	        this.fieldKind = typeEnum;
	        this.customClass = customClass;
	    }
	    
	    public FieldType(FieldKind typeEnum) {
            this.fieldKind = typeEnum;
            this.customClass = null;
        }
	    
	    public static final FieldType INTEGER = new FieldType(FieldKind.INTEGER);
	    public static final FieldType LONG = new FieldType(FieldKind.LONG);
	    public static final FieldType FLOAT = new FieldType(FieldKind.FLOAT);
	    public static final FieldType DOUBLE = new FieldType(FieldKind.DOUBLE);
	    public static final FieldType BIGDECIMAL = new FieldType(FieldKind.BIGDECIMAL);
	    public static final FieldType STRING = new FieldType(FieldKind.STRING);
	    public static final FieldType BOOL = new FieldType(FieldKind.BOOL);
	    public static final FieldType DATE = new FieldType(FieldKind.DATE);
	    public static final FieldType ENUM = new FieldType(FieldKind.ENUM);
	    public static final FieldType ENTITY = new FieldType(FieldKind.ENTITY);
	    
	    public static FieldType enumType(Class<?> klass) {
	        FieldType t = new FieldType(FieldKind.ENUM, klass);
	        return t;
	    }
	    
	    public FieldKind getKind() {
	        return fieldKind;
	    }
	    
	    public Class<?> getCustomClass() {
	        return customClass;
	    }
	    
	    @SuppressWarnings("unchecked")
	    public static Object convertType(Object value, FieldType type) {
	        if (type.fieldKind == FieldKind.DATE && (value instanceof Number)) {
	            Long date = Long.decode(value.toString());
	            return new Date(date);
	        }
	        if (type.fieldKind == FieldKind.INTEGER && (value instanceof Number)) {
	            return ((Number) value).intValue();
	        }
	        if (type.fieldKind == FieldKind.LONG && (value instanceof Number)) {
	            return ((Number) value).longValue();
	        }
	        if (type.fieldKind == FieldKind.BIGDECIMAL && (value instanceof Number)) {
	            return new BigDecimal(value.toString());
            }
	        if (type.fieldKind == FieldKind.DOUBLE && (value instanceof Double)) {
	            return ((Double) value).doubleValue();
	        }
	        if (type.fieldKind == FieldKind.ENUM && (value instanceof String)) {
	            @SuppressWarnings("rawtypes")
                Class<Enum> enumType = (Class<Enum>)type.customClass;
	            return Enum.valueOf(enumType, (String)value);
	        }
	        return value;
	    }
	    
	    public static List<Object> convertList(String filterName, Object value, FieldType type) {
	        if (! (value instanceof Collection<?>)) {
	            throw DetailedException.create(logger, QuerySpecErrors.QUERY_FILTER_HAS_WRONG_TYPE, filterName, "must be list");
	        }
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() == 0) {
                throw DetailedException.create(logger, QuerySpecErrors.EMPTY_LIST_QUERY_FILTER, filterName);
            }
            List<Object> list;
            list = new ArrayList<>();
            for (Object i : collection) {
                list.add(FieldType.convertType(i, type));
            }
            return list;
	    }
	}
	
}
