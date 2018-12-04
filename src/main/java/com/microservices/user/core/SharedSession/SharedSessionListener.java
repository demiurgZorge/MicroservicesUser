package com.microservices.user.core.SharedSession;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class SharedSessionListener implements ServletRequestListener {
    
    private static final String SHARED_KEY = "SharedSessionListenerKey";
    
    @Override
    public void requestDestroyed(ServletRequestEvent sre) {
        ServletContext context = sre.getServletContext();
        HttpServletRequest sr = (HttpServletRequest) sre.getServletRequest();
        
        synchronized (context) {
            HttpSession session = sr.getSession();
            
            HashMap<String, Object> is = getSharedSession(session);
            
            is.clear();
            Enumeration<String> en = session.getAttributeNames();
            while (en.hasMoreElements()) {
                String key = en.nextElement();
                is.put(key, session.getAttribute(key));
            }
        }
    }
    
    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        ServletContext context = sre.getServletContext();
        HttpServletRequest sr = (HttpServletRequest) sre.getServletRequest();
        
        synchronized (context) {
            
            HttpSession session = sr.getSession();
            
            HashMap<String, Object> is = getSharedSession(session);
            
            Iterator<String> it = is.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object val = is.get(key);
                session.setAttribute(key, val);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private HashMap<String, Object> getSharedSession(HttpSession session) {
        String key = SHARED_KEY + "_" + session.getId();
        ServletContext ctx = session.getServletContext().getContext("/");
        if (ctx == null) {
            return new HashMap<>();
        }
        HashMap<String, Object> srv = (HashMap<String, Object>) ctx.getAttribute(key);
        if (srv == null) {
            srv = new HashMap<>();
            ctx.setAttribute(key, srv);
        }
        
        return srv;
    }
}
