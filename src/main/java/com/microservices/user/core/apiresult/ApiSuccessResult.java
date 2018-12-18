package com.microservices.user.core.apiresult;

public class ApiSuccessResult<T> extends ApiResult {
	
	protected T data;
	
	protected ApiSuccessResult() {
		status = true;
	}
	
	public ApiSuccessResult(T data) {
		status = true;
		this.data = data;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
