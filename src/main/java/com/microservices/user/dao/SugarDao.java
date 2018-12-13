package com.microservices.user.dao;

import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.DAOTemplate;
import com.microservices.user.db.models.Sugar;
@Component
public class SugarDao extends DAOTemplate<Long, Sugar> {
    
    public SugarDao() {
        super(Sugar.class, "id");
    }

}
