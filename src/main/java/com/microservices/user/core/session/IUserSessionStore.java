package com.microservices.user.core.session;

import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public interface IUserSessionStore {
    
    public static enum Errors implements ErrorCodeEnum {
        USER_SESSION_NOT_FOUND_OR_EXPIRED("User session not found or expired");
        
        private String message;
        
        private Errors(String message) {
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
    
    void save(UserSessionDto session);
    UserSessionDto getCurrentSession();
    Long checkLoggedUserId();
    
}
