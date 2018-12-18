package com.microservices.user.core.apiresult;

import java.util.List;

import org.slf4j.Logger;

import com.microservices.user.core.dao.QueryMetaInformation;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.dao.exceptions.BaseErrors;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.DetailedException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

public class ApiResult {
	
	protected Boolean status = false;

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public static ApiErrorResult fail(String code, String message) {
		return new ApiErrorResult(code, message);
	}
	
	public static ApiErrorResult fail(ErrorCodeEnum error) {
		return new ApiErrorResult(error);
	}
	
	public static ApiErrorResult fail(Throwable t) {
		return new ApiErrorResult(t);
	}
	
    public static ApiErrorResult fail(BaseException exception) {
		if (exception instanceof DetailedException) {
			ApiDataErrorResult result = ApiResult.fail((DetailedException) exception);
			return result;
		}
        return new ApiErrorResult(exception);
    }
	
    public static ApiDataErrorResult fail(ErrorCodeEnum errorCode, Object... data) {
        ApiDataErrorResult result = new ApiDataErrorResult(errorCode, data);
        return result;
    }
    
    public static ApiDataErrorResult fail(DetailedException exception){
        ApiDataErrorResult result = new ApiDataErrorResult(exception);
        return result;
    }
    
//	public static ApiErrorResult unauthorized() {
//		return fail(AuthErrorCode.NOT_AUTHORIZED);
//	}
	
	public static ApiSuccessResult<Boolean> success() {
		return new ApiSuccessResult<>(true);
	}
	
	public static <T> ApiSuccessResult<T> success(T data) {
		return new ApiSuccessResult<T>(data);
	}
	
	public static <T> ApiResult successIfNotNull(T data, ErrorCodeEnum error) {
		if (data != null) {
			return new ApiSuccessResult<T>(data);
		}
		else {
			return fail(error);
		}
	}
	
    public static <L extends List<?>> ApiListResult<L> list(L data, QueryMetaInformation info) {
        return new ApiListResult<>(data, info);
    }

    public static <L extends List<?>> ApiListResult<L> list(L data) {
        QueryMetaInformation info = new QueryMetaInformation(null, -1L);
        return list(data, info);
    }

	public static <L extends List<?>> ApiListResult<L> list(L data, Long recordCount) {
		QueryMetaInformation info = new QueryMetaInformation(null, recordCount);
		return list(data, info);
	}

    public static <L extends List<?>> ApiListResult<L> list(QueryState query, L data, Long recordCount) {
        QueryMetaInformation info = new QueryMetaInformation(query, recordCount);
        return list(data, info);
    }

    public static <L extends List<?>> ApiListResult<L> list(QueryState query, L data) {
        return list(query, data, (long) data.size());
    }

    public static <T> ApiListResult<List<ApiOperationStatus<T>>> create(List<ApiOperationStatus<T>> list) {
        boolean status = true;
        for(ApiOperationStatus<T> op : list ) {
            status = status && op.isStatus();
        }
        return (status ? new ApiListResult<>(list, null) : ApiResult.fail(list));
    }
    
    private static <T> ApiListResult<List<ApiOperationStatus<T>>> fail(List<ApiOperationStatus<T>> list) {
        ApiListResult<List<ApiOperationStatus<T>>> result = new ApiListResult<>(list, null);
        result.setStatus(false);
        return result;
    }

    public static <T> ApiMapResult<T> map(QueryState query, ApiMapEnum key, T data, Long recordCount) {
        QueryMetaInformation info = new QueryMetaInformation(query, recordCount);
        return new ApiMapResult<>(key, data, info);
    }
    
    
    @SuppressWarnings("unchecked")
    public static <K extends Enum<K>, T>  T checkData(Logger logger, ApiResult result, Class<K> ...enums) {
        if (result.status) {
            ApiSuccessResult<T> successResult = (ApiSuccessResult<T>)result;
            return successResult.getData();
        }
        if (! (result instanceof ApiErrorResult)) {
            throw BaseException.create(logger, BaseErrors.UNKNOWN);
        }
        
        ApiErrorResult errorResult = (ApiErrorResult)result;
        for(Class<K> en : enums) {
            try {
                ErrorCodeEnum val = (ErrorCodeEnum)Enum.valueOf(en, errorResult.code);
                throw DetailedException.create(logger, val);
            }
            catch(Exception ex) {
                // nothing here
            }
        }
        throw BaseException.create(logger, BaseErrors.UNKNOWN);
    }
    
}
