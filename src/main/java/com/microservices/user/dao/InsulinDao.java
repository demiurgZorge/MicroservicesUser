package com.microservices.user.dao;

import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.DAOTemplate;
import com.microservices.user.db.models.Insulin;

@Component
public class InsulinDao extends DAOTemplate<Long, Insulin> {
    public InsulinDao() {
        super(Insulin.class, "id");
    }
}
