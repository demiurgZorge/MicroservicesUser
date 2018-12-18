package com.microservices.user.core.interfaces;

public class QuerySpecificationAlias {
	
    public static enum JoinType {
        INNER, LEFT;
    }
    
    public String path;
	public String name;
	public JoinType joinType = JoinType.INNER;
	
	public QuerySpecificationAlias(String path, String alias) {
		this.path = path;
		this.name = alias;
	}
}