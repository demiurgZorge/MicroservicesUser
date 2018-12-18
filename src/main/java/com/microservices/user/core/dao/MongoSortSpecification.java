package com.microservices.user.core.dao;

import com.microservices.user.core.interfaces.SortEnum;
import com.microservices.user.core.interfaces.SortSpecification;

public class MongoSortSpecification<T> implements SortSpecification<T> {
	
	private String name;
	
	private String expression;
	
	public MongoSortSpecification(String name, String expr) {
		this.name = name;
		this.expression = expr;
	}
	
	@Override
	public String getName() {
		return name;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public static <T> MongoSortSpecification<T> create(SortEnum sort, String expr) {
	    return new MongoSortSpecification<T>(sort.toString(), expr);
	}
}
