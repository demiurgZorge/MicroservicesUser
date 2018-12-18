package com.microservices.user.core.apiresult;

import java.util.HashMap;
import java.util.Map;

import com.microservices.user.core.dao.QueryMetaInformation;

public class ApiMapResult<T> extends ApiSuccessResult<Map<String, Object>> {
	
	private QueryMetaInformation metaInformation;
	
	public ApiMapResult(ApiMapEnum key, T data, QueryMetaInformation info) {
		Map<String, Object> map = new HashMap<>();
		map.put(key.toString(), data);
		this.data = map;
		this.metaInformation = info;
	}
	
	public QueryMetaInformation getMetaInformation() {
		return metaInformation;
	}

	public void setMetaInformation(QueryMetaInformation metaInformation) {
		this.metaInformation = metaInformation;
	}
	
	public void addResult(ApiMapEnum key, Object result) {
		this.data.put(key.toString(), result);
	}
}
