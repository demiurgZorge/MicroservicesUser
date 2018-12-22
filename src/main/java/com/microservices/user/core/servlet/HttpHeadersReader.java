package com.microservices.user.core.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import eu.bitwalker.useragentutils.UserAgent;

@Component
public class HttpHeadersReader {
    
    public enum LogHeaders {
    	IP("IP"),
    	NGINX_IP("X-Real-IP"),
    	NGINX_FWD("X-Forwarded-For"),
    	USER_AGENT("User-Agent"),
        BROWSER_NAME("BROWSER_NAME"),
        BROWSER_TYPE("BROWSER_TYPE"), 
        BROWSER_VERSION("BROWSER_VERSION"), 
        DEVICE_OS("DEVICE_OS"), 
        DEVICE_TYPE("DEVICE_TYPE"), 
        LOGIN_APP("LOGIN_APP");
    	
    	private final String headerCode;
    	
    	private LogHeaders(final String headerCode) {
            this.headerCode = headerCode;
        }
    	
    	@Override
        public String toString() {
            return headerCode;
        }
    }
	
	/**
     * Сборка списка заголовков из запроса
     * 
     * @param httpRequest	Запрос
     * @return				Map<String, Object>
     */
	public Map<String, Object> getHttpHeaders(HttpServletRequest httpRequest) {
    	Map<String, Object> headers = new HashMap<String, Object>();
    	
		for (LogHeaders header: LogHeaders.values()) {
			if (httpRequest.getHeader(header.toString()) != null) {
				String value = httpRequest.getHeader(header.toString());
				headers.put(header.toString(), value);
			}
		}
		headers.put(LogHeaders.IP.toString(), httpRequest.getRemoteHost());
		headers.put(LogHeaders.LOGIN_APP.toString(), "PHOTOSITE");
		
		if (!headers.containsKey(LogHeaders.USER_AGENT.toString())) {
			return headers;
		}
		
		UserAgent userAgent = UserAgent.parseUserAgentString(httpRequest.getHeader(headers.get(LogHeaders.USER_AGENT.toString()).toString()));
		if (userAgent.getBrowser() != null) {
			headers.put(LogHeaders.BROWSER_NAME.toString(), userAgent.getBrowser().getName());
			if (userAgent.getBrowser().getBrowserType() != null) {
				headers.put(LogHeaders.BROWSER_TYPE.toString(), userAgent.getBrowser().getBrowserType().getName());
			}
		}
		if (userAgent.getBrowserVersion() != null) {
			headers.put(LogHeaders.BROWSER_VERSION.toString(), userAgent.getBrowserVersion().getVersion());
		}
		if (userAgent.getOperatingSystem() != null) {			
			headers.put(LogHeaders.DEVICE_OS.toString(), userAgent.getOperatingSystem().getName());
			if (userAgent.getOperatingSystem().getDeviceType() != null) {
				headers.put(LogHeaders.DEVICE_TYPE.toString(), userAgent.getOperatingSystem().getDeviceType().getName());
			}
		}
		
		return headers;
    } 
}
