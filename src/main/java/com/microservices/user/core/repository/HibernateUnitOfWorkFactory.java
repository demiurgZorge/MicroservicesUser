package com.microservices.user.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.microservices.user.core.interfaces.IUnitOfWork;
import com.microservices.user.core.interfaces.IUnitOfWorkFactory;

@Component("HibernateUnitOfWorkFactory")
public class HibernateUnitOfWorkFactory implements IUnitOfWorkFactory {

    @Autowired
    ApplicationContext ctx;
    
    @Override
    public IUnitOfWork create() {
        
        return ctx.getBean(HibernateUnitOfWork.class);
    }
    
    
    
}
