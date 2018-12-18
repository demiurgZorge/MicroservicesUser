package com.microservices.user.core.apiresult;

import java.util.List;

import com.microservices.user.core.dao.QueryMetaInformation;

public class ApiListResult<L extends List<?>> extends ApiSuccessResult<L> {
	
	private QueryMetaInformation metaInformation;
	
	public ApiListResult(L data, QueryMetaInformation info) {
		super(data);
		this.metaInformation = info;
	}

	public QueryMetaInformation getMetaInformation() {
		return metaInformation;
	}

	public void setMetaInformation(QueryMetaInformation metaInformation) {
		this.metaInformation = metaInformation;
	}
	
}
