package com.microservices.user.core.session;

import org.slf4j.Logger;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public class AuthException extends BaseException {
    
    private static final long serialVersionUID = 5264196467954524590L;
    public static final String LOGGER_MESSAGE = "AUTH-ERROR";
    
    public static enum AuthErrorCode implements ErrorCodeEnum {
        AUTH_ERROR("Authorisation error"),
        AUTH_OPERATION_NOT_ALLOWED("Operation not allowed"),
        NOT_AUTHORIZED("Not authorized"),
        ADMIN_NOT_AUTHORIZED("Admin not authorized"),
        AUTH_TOKEN_TIMEOUT("Auth token did expired"),
        ADMIN_HAVE_NO_RIGHT_TO_MANAGE_PHOTO("Admin must be in manage photo group!"),
        ADMIN_HAVE_NO_RIGHT_TO_MANAGE_USERS("Admin must be in manage users group!"),
        ADMIN_IS_NOT_PRIVELEGED("Admin is not privileged admin!"),
        ADMIN_REQUESTED_BY_ID_IS_ABSENT("AdminUser requested by id is absent"),
        CURRENT_USER_IS_NOT_MARKET_ADMIN("Current user is not MarketAdmin");
        
        private final String text;

        private AuthErrorCode(final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return text;
        }

        @Override
        public String code() {
            return this.name();
        }
        
    }

    
    public static AuthException NotAuthorized() {
        AuthException e = new AuthException(AuthErrorCode.NOT_AUTHORIZED);
        return e;
    }

    public static AuthException AdminNotAuthorized() {
        AuthException e = new AuthException(AuthErrorCode.ADMIN_NOT_AUTHORIZED);
        return e;
    }
    
    public static AuthException AuthError(Logger logger) {
        AuthException e = new AuthException(AuthErrorCode.AUTH_ERROR);
        logger.error(LOGGER_MESSAGE, e);
        return e;
    }

    public AuthException(Throwable cause) {
        super(cause);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(AuthErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(AuthErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
}
