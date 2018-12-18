package com.microservices.user.core.interfaces;

import com.microservices.user.core.dao.exceptions.DetailedException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public class RepositoryException extends DetailedException {
    
    private static final long serialVersionUID = 1L;
    
    public RepositoryException(ErrorCodeEnum errorCode, Throwable cause) {
        super(errorCode, cause);
    }


}
