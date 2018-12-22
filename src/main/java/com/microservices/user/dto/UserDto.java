package com.microservices.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.microservices.user.db.models.User;

@JsonInclude(Include.NON_NULL)
public class UserDto {
    public Long   id;
    public String name;
    public String authToken;
    public boolean active;
    public String ip;
    
    public UserDto() {
        super();
    };
    
    public static UserDto create(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.active = user.getActive();
        dto.ip = user.getIp();
        return dto;
    }
    
    public static UserDto createWithToken(User user, String token) {
        UserDto dto = create(user);
        dto.authToken = token;
        return dto;
    }
}
