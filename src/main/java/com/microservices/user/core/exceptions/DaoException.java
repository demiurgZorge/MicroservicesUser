package com.microservices.user.core.exceptions;

import org.slf4j.Logger;

public class DaoException extends BaseException {
    
    public enum DaoErrors implements ErrorCodeEnum {
        UNKNOWN_DAO_ERROR("unknown dao error got his way to life"), ID_FIELD_NOT_SUPPLIED(
                "Id field not supplied to DAO constructor");
        
        private String message;
        
        private DaoErrors(String message) {
            this.message = message;
        }
        
        @Override
        public String code() {
            return this.name();
        }
        
        @Override
        public String toString() {
            return this.message + " : " + code();
        }
    }
    
    public DaoException(Throwable t) {
        super(t);
    }
    
    public DaoException(ErrorCodeEnum errorCode) {
        super(errorCode);
    }
    
    private static final long serialVersionUID = 1L;
    
    public static DaoException IdFieldEmpty() {
        DaoException e = new DaoException(DaoErrors.ID_FIELD_NOT_SUPPLIED);
        e.printStackTrace();
        return e;
    }
    
    public static DaoException create(Throwable t) {
        DaoException e = new DaoException(t);
        t.printStackTrace();
        return e;
    }
    
    public static DaoException create(Logger logger, Throwable t) {
        DaoException e = new DaoException(t);
        logger.error("DAO-ERROR", t);
        return e;
    }
}
