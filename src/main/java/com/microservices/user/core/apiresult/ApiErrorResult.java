package com.microservices.user.core.apiresult;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.DetailedException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public class ApiErrorResult extends ApiResult {
	
	protected String message;
	protected String code;
	protected String codeType;
	protected Throwable exception;
	
	public ApiErrorResult(ErrorCodeEnum errorCode) {
	    super();
	    this.code = errorCode.code();
	    this.codeType = errorCode.getClass().getSimpleName();
	    this.message = errorCode.toString();
	}
	
	public ApiErrorResult(String code, String message) {
		super();
	    this.code = code;
	    this.codeType = "UNKNOWN";
		this.message = message;
	}
	
	public ApiErrorResult(BaseException exception) {
	    this(exception.getErrorCode());
        init(exception);
	}
	
	public ApiErrorResult(DetailedException exception) {
	    this(exception.getErrorCode());
        init(exception);
    }

    private void init(BaseException exception) {
        if ( exception.getMessage() != null) {
            this.message = exception.getMessage();
        }
        this.exception = exception;
    }
	
	public ApiErrorResult(Throwable exception) {
	    super();
	    this.exception = exception;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }
}
