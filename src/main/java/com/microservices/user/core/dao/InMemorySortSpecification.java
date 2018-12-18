package com.microservices.user.core.dao;

import java.util.Comparator;

import com.microservices.user.core.interfaces.SortSpecification;

public abstract class InMemorySortSpecification<T> implements SortSpecification<T> {
	
	public abstract Comparator<T> getComparator();
	
	public static <T> InMemorySortSpecification<T> createDefault(
			final String name,
			final Comparator<T> comp ) {
		return new InMemorySortSpecification<T>() {
		
			@Override
			public Comparator<T> getComparator() {
				return comp;
			}

			@Override
			public String getName() {
				return name;
			}

            @Override
            public String getExpression() {
                return null;
            }
		};
	}
}
