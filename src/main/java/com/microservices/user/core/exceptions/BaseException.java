package com.microservices.user.core.exceptions;

import org.slf4j.Logger;

public class BaseException extends RuntimeException {
    
    private static final long serialVersionUID = 8739998487480187268L;
    protected ErrorCodeEnum   errorCode;
    protected String          message;
    
    public BaseException(Throwable cause) {
        super(cause);
        this.errorCode = BaseErrors.UNKNOWN;
        this.message = cause.getMessage();
    }
    
    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = BaseErrors.UNKNOWN;
        this.message = message;
    }
    
    public BaseException(ErrorCodeEnum errorCode) {
        this.errorCode = errorCode;
    }
    
    public BaseException(ErrorCodeEnum errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
        this.message = cause.getMessage();
    }
    
    public BaseException(ErrorCodeEnum errorCode, String message) {
        this.message = message;
        this.errorCode = errorCode;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
    
    public ErrorCodeEnum getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        String s = "ERROR: " + errorCode.code();
        if (this.message != null) {
            s = s + " \n Message: " + this.message;
        }
        return s;
    }
    
    public static BaseException unknown(Logger logger, Throwable cause) {
        BaseException e = new BaseException(BaseErrors.UNKNOWN, cause);
        logger.error(e.toString(), e);
        return e;
    }
    
    public static BaseException requiredParameterIsEmptyOrNull(Logger logger) {
        BaseException e = new BaseException(BaseErrors.REQUIRED_PARAMETERS_EMPTY_OR_NULL);
        logger.error(e.toString(), e);
        return e;
    }
    
    public static BaseException create(Logger logger, ErrorCodeEnum error) {
        BaseException e = new BaseException(error);
        logger.error(e.toString(), e);
        return e;
    }
    
    public static BaseException create(Logger logger, ErrorCodeEnum error, String msg) {
        BaseException e = new BaseException(error, msg);
        logger.error(e.toString(), e);
        return e;
    }
    
}
