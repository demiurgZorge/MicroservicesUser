package com.microservices.user.dto;

import com.microservices.user.db.models.User;

public class CreateUserDto {

	public String name;
	
	public CreateUserDto(){
		super();
	}

	public User create() {
		User user = new User();
		user.setName(this.name);
		return user;
	}
}
