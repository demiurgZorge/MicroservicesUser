package com.microservices.user.core.session;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserSessionDto {
    
    private String token;
    
    @JsonIgnore
    private String apiToken;
    
    private Long userId;
    
    private String sessionCookie;
    
    private String firstName = "";

    private String lastName = "";

    public UserSessionDto() {
        
    }
    
    public UserSessionDto(String token, Long userId) {
        super();
        this.token = token;
        this.userId = userId;
    }
           
	public UserSessionDto(String token, Long userId, String sessionCookie, String email) {
		super();
		this.token = token;
		this.userId = userId;
		this.sessionCookie = sessionCookie;
	}

	public UserSessionDto(String token, Long userId, String sessionCookie, String firstName, String lastName,
			String phone, String phoneCode, String email) {
		super();
		this.token = token;
		this.userId = userId;
		this.sessionCookie = sessionCookie;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

	public String getSessionCookie() {
		return sessionCookie;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	
	@JsonIgnore
	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
}
