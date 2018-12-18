package com.microservices.user.core.dao.exceptions;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMapFormatter {
    
    public Map<String, Object> format(Throwable ex) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("message", ex.getMessage());
        result.put("type", ex.getClass().getName());
        result.put("details", ex.toString());
        if (ex.getCause() != null && ex.getCause() != ex) {
            result.put("cause", format(ex.getCause()));
        }
        result.put("stack", ex.getStackTrace());
        return result;
    }
    
}
