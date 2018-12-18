package com.microservices.user.core.apiresult;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

/**
 * Created by komp1 on 8/5/2016.
 */
public class ApiOperationStatus<T>{
    private Long id;
    private boolean status;
    private T data;
    private String message;
    private String code;
    private Throwable exception;

    public ApiOperationStatus(){};

    public ApiOperationStatus(Long id, String errorCode, String errorMsg) {
        this.id = id;
        this.code = errorCode;
        this.message = errorMsg;
        this.status = false;
    }

    public ApiOperationStatus(String errorCode, String errorMsg) {
        this.code = errorCode;
        this.message = errorMsg;
        this.status = false;
    }

    public ApiOperationStatus(BaseException exception) {
        this.message = exception.getMessage();
        this.code = exception.getErrorCode().code();
        this.exception = exception;
        this.status = false;
    }
    
    public ApiOperationStatus(Long id, BaseException exception) {
        this(exception);
        this.id = id;
    }
    
    public ApiOperationStatus(Long id, T data, String code){
        this.id = id;
        this.data = data;
        this.code = code;
        this.status = true;
    }

    public ApiOperationStatus(Throwable exception) {
        this.exception = exception;
        this.status = false;
    }
    
    public ApiOperationStatus(Long id, Throwable exception) {
        this(exception);
        this.id = id;
    }
    
    public static <T> ApiOperationStatus<T> success(Long id, T data){
        return new ApiOperationStatus<T>(id, data, null);
    }

    public static <T> ApiOperationStatus<T> fail(Long id, String errorCode, String errorMsg){
        return new ApiOperationStatus<>(id, errorCode, errorMsg);
    }

    public static <T> ApiOperationStatus<T> fail(Long id, ErrorCodeEnum error) {
        return new ApiOperationStatus<>(id, error.code(), error.toString());
    }

    public static <T> ApiOperationStatus<T> fail(String errorCode, String errorMsg){
        return new ApiOperationStatus<>(errorCode, errorMsg);
    }

    public static <T> ApiOperationStatus<T> fail(Exception exception) {
        if (exception instanceof BaseException) {
            ApiOperationStatus<T> result = new ApiOperationStatus<>((BaseException) exception);
            return result;
        }
        return new ApiOperationStatus<>(exception);
    }

    public static <T> ApiOperationStatus<T> fail(Long id, Exception exception) {
        if (exception instanceof BaseException) {
            ApiOperationStatus<T> result = new ApiOperationStatus<>(id, (BaseException) exception);
            return result;
        }
        return new ApiOperationStatus<>(id, exception);
    }
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
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
