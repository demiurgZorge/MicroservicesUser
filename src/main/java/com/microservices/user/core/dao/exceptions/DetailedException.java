package com.microservices.user.core.dao.exceptions;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DetailedException extends BaseException {

    private static final long serialVersionUID = -4018882823667141144L; 
       
    private Object data = null;
    
    public DetailedException(ErrorCodeEnum errorCode, Throwable cause) {
        super(errorCode, cause);
    }
    
    public DetailedException(ErrorCodeEnum errorCode) {
        super(errorCode);
    }
    
    public DetailedException(ErrorCodeEnum errorCode, String message) {
        super(errorCode, message);
    }

    public DetailedException(ErrorCodeEnum errorCode, Map<String, Object> data) {
        super(errorCode);
        this.data = data;
    }

    public Object getData() {
        return data;
    }


    public void setData(Map<String, Object> data) {
        this.data = data;
    }
    
    public void setData(Object data) {
        //ObjectMapper mapper  = new ObjectMapper();
        this.data = data; //mapper.convertValue(data, Map.class);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public String getMessage() {
        String result = null;
        Object[] params = null;
        String msg = (this.message != null) ? this.message : errorCode.toString();
       
        String msgData = "";
        
        if (data instanceof Map) {
            Map<String, Object> m = (Map<String, Object>)data;
            for(Entry<String, Object> entry: m.entrySet()) {
                msgData = msgData + "\n "+ entry.getKey() + ": " + entry.getValue();
            }
            params = m.values().toArray(); 
        }
        else if (data instanceof Collection) {
            List<Object> l = (List<Object>)data;
            for(Object o : l) {
                msgData = msgData + "\n " + o;
            }
            params = l.toArray();
        }
        else if(data != null) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> m = mapper.convertValue(data, Map.class);
            for(Entry<String, Object> entry: m.entrySet()) {
                msgData = msgData + "\n "+ entry.getKey() + ": " + entry.getValue();
            }
            params = m.values().toArray(); 
        }
        
        if (params == null) {
            return msg;
        }
        else {
            result = String.format(msg, params);
        }
        return result;
    }
    
    @Override
    public String toString() {
        return "ERROR: " + errorCode.code() + 
               "\n message: " + getMessage();
    }

    public static DetailedException createWithMap(ErrorCodeEnum error, Object... data) {
        return createWithMap(defaultLogger, error, data);
    }
    
    public static DetailedException createWithMap(Logger logger, ErrorCodeEnum error, Object... data) {
        DetailedException e = new DetailedException(error, dataToMap(data));
        logger.error(e.toString());
        return e;
    }
    
    public static DetailedException create(Logger logger, ErrorCodeEnum error, Object... data) {
        DetailedException e = new DetailedException(error);
        e.setData(Arrays.asList(data));
        logger.error(e.toString());
        return e;
    }
    
    public static DetailedException createWithDto(Logger logger, ErrorCodeEnum error, Object data) {
        DetailedException e = new DetailedException(error);
        e.setData(data);
        logger.error(e.toString());
        return e;
    }
    
    public static Map<String, Object> dataToMap(Object... data) {
        Map<String, Object> result = new LinkedHashMap<>();
        Long k = 0L;
        for(Object obj: data) {
            result.put(k.toString(), obj);
            k++;
        }
        return result;
    }
    
}
