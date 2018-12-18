package com.microservices.user.core.dao;

import org.hibernate.validator.internal.xml.FieldType;

import com.microservices.user.core.interfaces.FilterEnum;
import com.microservices.user.core.interfaces.QueryFilter;
import com.microservices.user.core.interfaces.QueryFilter.FieldKind;

public class EmptyQueryFilter implements QueryFilter {
	
	private String name;
	
    public EmptyQueryFilter(String filterName) {
        this.name = filterName;
    }
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public FieldType getFieldType() {
		return new FieldType(FieldKind.ENTITY);
	}
	
	public static EmptyQueryFilter create(final FilterEnum filter) {
		return new EmptyQueryFilter(filter.toString());
	}
	
    public static EmptyQueryFilter create(String filterName) {
        return new EmptyQueryFilter(filterName);
    }
}
