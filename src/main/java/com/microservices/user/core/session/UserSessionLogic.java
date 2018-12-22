package com.microservices.user.core.session;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.microservices.user.core.SharedSession.CC;
import com.microservices.user.core.SharedSession.CrosscontextUtils;
import com.microservices.user.core.crypto.CryptoToken;
import com.microservices.user.core.crypto.UserAuthTokenGeneratorService;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.servlet.HttpHeadersReader.LogHeaders;
import com.microservices.user.db.models.User;


@Component
public class UserSessionLogic {

    public static class UserSessionDto {
        private Long userId;
        private String userName;
        private Boolean isAdult;
        private String authToken;
        private Map<String, Object> httpHeaders = new HashMap<>();
        
        public UserSessionDto() {
            
        }
        
        public UserSessionDto(com.microservices.user.db.models.User user, String authToken) {
            this.userId = user.getId();
            this.authToken = authToken;
        }
        
        public UserSessionDto addHeaders(Map<String, Object> httpHeaders) {
            httpHeaders.putAll(httpHeaders);
            return this;
        }
        
        public Long getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }

        public Boolean getIsAdult() {
            return isAdult;
        }

        public String getAuthToken() {
            return authToken;
        }

        @JsonIgnore
        public String getIp() {
            Object ip = httpHeaders.get(LogHeaders.IP.toString());
            return (ip != null ) ? ip.toString() : null;
        }

        @JsonIgnore
        public String getUserAgent() {
            Object agent =  httpHeaders.get(LogHeaders.USER_AGENT.toString());
            return (agent != null) ? agent.toString() : "";
        }
    }
    
    private static final Logger logger = LoggerFactory.getLogger(UserSessionLogic.class);
    
    @Autowired
    protected HttpSession session;
    
    @Autowired
    private UserAuthTokenGeneratorService authTokenGenerator;
    
    public UserSessionDto saveCurrentUser(User user, Map<String, Object> httpHeaders) {
        String token = authTokenGenerator.generate(user.getId(), session.getId());
        return saveCurrentUser(user, token, httpHeaders);
    }
    
    public UserSessionDto saveCurrentUser(User user, String authToken, Map<String, Object> httpHeaders) {
        UserSessionDto dto = new UserSessionDto(user, authToken).addHeaders(httpHeaders);
        try {
            CrosscontextUtils.storeObjectAsJson(session, CC.SESSION__CURRENT_USER_ID, dto);
            return dto;
        } catch (Exception e) {
            throw BaseException.unknown(logger, e);
        }
    }

    public Long checkLoggedUserId() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            throw AuthException.NotAuthorized();
        }
        return userId;
    }

    public UserSessionDto checkLoggedUser() {
        UserSessionDto user = getCurrentUser();
        if (user == null) {
            throw AuthException.NotAuthorized();
        }
        return user;
    }

    public boolean isUserLogged(Long userId) {
        Long logged = getCurrentUserId();
        return (logged == null) ? false : logged.equals(userId);
    }

    public UserSessionDto getCurrentUser() {
        try {
            UserSessionDto user = CrosscontextUtils.getJsonObject(session, CC.SESSION__CURRENT_USER_ID, UserSessionDto.class);
            return user;
        }
        catch (ClassCastException | IOException e) {
            throw BaseException.unknown(logger, e);
        }
    }

    public void signOut (HttpServletResponse response) {
        session.setAttribute(CC.IS_LOGGED_TO_MARKET, false);
        session.removeAttribute(CC.SESSION__CURRENT_USER_ID);
        session.invalidate();
        Cookie cookie = new Cookie ("JSESSIONID", "SESSION_EXPIRE");
        cookie.setPath("/");
        cookie.setMaxAge( 0 );
        response.addCookie(cookie);
    }

    public Long getCurrentUserId() {
        UserSessionDto user = getCurrentUser();
        return (user != null) ? user.getUserId() : null;
    }
    
    public String getSessionHash(){
        return CryptoToken.get(session.getId(), String.valueOf(System.currentTimeMillis()));
    }
    public String getSessionHash(String str){
        return CryptoToken.get(session.getId(), str);
    }
    
}
