package com.microservices.user.core.dao.exceptions;

public enum QuerySpecErrors implements ErrorCodeEnum {
    QUERY_FILTER_HAS_WRONG_TYPE("Filter %s has wrong type. Must be %s"),
    EMPTY_LIST_QUERY_FILTER("Filter %s has type list but it empty");

    private String message;
    
    private QuerySpecErrors(String msg) {
        this.message = msg;
    }
    
    @Override
    public String code() {
        return name();
    }
    
    @Override
    public String toString() {
        return message;
    }
}