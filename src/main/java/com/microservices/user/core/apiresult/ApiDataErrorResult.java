package com.microservices.user.core.apiresult;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.DetailedException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public class ApiDataErrorResult extends ApiErrorResult{
    public Object errorData;

    
    public ApiDataErrorResult(BaseException exception, Object... errorData) {
        super(exception);
        this.errorData = DetailedException.dataToMap(errorData);
    }
    
    public ApiDataErrorResult(ErrorCodeEnum errorCode, Object... errorData) {
        super(errorCode);
        this.errorData = DetailedException.dataToMap(errorData);
        this.message = String.format(errorCode.toString(), errorData);
    }
    
    public ApiDataErrorResult(DetailedException exception) {
        super(exception);
        this.errorData = exception.getData();
    }
    
    public Object getErrorData() {
        return errorData;
    }


    public void setErrorData(Object... errorData) {
        this.errorData = DetailedException.dataToMap(errorData);
    }
}
