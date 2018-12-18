package com.microservices.user.core.dao;

import com.microservices.user.core.interfaces.SortEnum;
import com.microservices.user.core.interfaces.SortSpecification;

public abstract class HibCriteriaSortSpecification<T> implements SortSpecification<T> {
	
	public abstract String getExpression();
	
	public static <T> HibCriteriaSortSpecification<T> create(
			final SortEnum  name, final String expr) {
		return new HibCriteriaSortSpecification<T>() {
			
			@Override
			public String getName() {
				return name.toString();
			}
			
			public String getExpression() {
				return expr;
			}
		};
	}
}
