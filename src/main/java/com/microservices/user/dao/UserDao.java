package com.microservices.user.dao;

import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.DAOTemplate;
import com.microservices.user.db.models.User;

@Component
public class UserDao extends DAOTemplate<Long, User> {

	public UserDao() {
        super(User.class, "id");
    }
}
