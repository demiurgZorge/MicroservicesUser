package com.microservices.user.core.SharedSession;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrosscontextUtils {
    
    public static void storeObjectAsJson(HttpSession session, String key, Object object)
        throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(object);
        session.setAttribute(key, json);
    }
    
    public static <T> T getJsonObject(HttpSession session, String key, Class<T> klass) throws IOException,
        ClassCastException {
        String json = (String) session.getAttribute(key);
        
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, klass);
    }
    
    public static void removeAttribute(HttpSession session, String key) throws IOException, ClassCastException {
        session.removeAttribute(key);
    }
    
    public static Long getLong(HttpSession session, String key) {
        Object o = session.getAttribute(key);
        if (o instanceof Long) {
            return (Long) o;
        }
        if (o instanceof Integer) {
            return ((Integer) o).longValue();
        }
        return null;
    }
}
