package com.microservices.user.core.dao;

public class QueryMetaInformation {
	
	private QueryState query;
	private Long recordCount = 0L;
	
	public QueryMetaInformation(QueryState query, Long recordCount) {
		this.query = query;
		this.recordCount = recordCount;
	}

	public QueryState getQuery() {
		return query;
	}
	
	public void setQuery(QueryState query) {
		this.query = query;
	}
	
	public Long getRecordCount() {
		return recordCount;
	}
	
	public void setRecordCount(Long totalRecords) {
		this.recordCount = totalRecords;
	}
	
}