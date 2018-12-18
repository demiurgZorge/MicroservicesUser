package com.microservices.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.microservices.user.db.models.User;

@JsonInclude(Include.NON_NULL)
public class UserDto {
    public Long   id;
    public String name;
    
    public UserDto() {
        super();
    };
    
    public static UserDto create(User user) {
        UserDto dto = new UserDto();
        dto.id = user.getId();
        dto.name = user.getName();
        return dto;
    }
}
