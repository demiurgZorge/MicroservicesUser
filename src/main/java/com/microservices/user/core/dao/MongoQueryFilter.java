package com.microservices.user.core.dao;

import java.util.Date;

import com.microservices.user.core.interfaces.QueryFilter;

/** 
 * Use MongoCriteriaFilter
 */
@Deprecated 
public abstract class MongoQueryFilter implements QueryFilter {
	public abstract String getFilterExpression(Object value);
	
	private String name;
	private FieldType fieldType;
	
	public MongoQueryFilter(String name, FieldType type) {
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
	
	protected static String parameterToString(Object o) {
		String v = "";
		if (o instanceof String) {
			v = "'" + o.toString() + "'";
		}
		else if (o instanceof Date) {
		    Long time = ((Date) o).getTime();
			v = time.toString();
		}
		else {
			v = o.toString();
		}
		return v;
	}
	
	public static MongoQueryFilter eq(
			final String name, final String field, final QueryFilter.FieldType type) {
		return new MongoQueryFilter(name, type) {
		
			@Override
			public String getFilterExpression(Object value) {
				return String.format("%s: %s", field, parameterToString(value));
			}
		};
	}
	
	public static MongoQueryFilter createWithExpression(
			final String name, final String expression, final QueryFilter.FieldType type) {
		return new MongoQueryFilter(name, type) {
			
			@Override
			public String getFilterExpression(Object value) {
				return String.format(expression, parameterToString(value));
			}
		};
	}

	public static MongoQueryFilter ilike(final String name, final String field) {
		return new MongoQueryFilter(name, FieldType.STRING) {

			@Override
			public String getFilterExpression(Object value) {
				return String.format("%s: { $regex: %s, $options: 'i' }", field,  parameterToString(value));
			}
			
		};
	}

}
