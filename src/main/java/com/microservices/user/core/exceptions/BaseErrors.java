package com.microservices.user.core.exceptions;

public enum BaseErrors implements ErrorCodeEnum {
    UNKNOWN("Unknown error"), OBJECT_NOT_FOUND("Object with such id not exists"), REQUIRED_PARAMETERS_EMPTY_OR_NULL(
            "Required parameters not supplied");
    
    private String message;
    
    private BaseErrors(String message) {
        this.message = message;
    }
    
    @Override
    public String code() {
        return this.name();
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return message;
    }
    
}
