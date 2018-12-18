package com.microservices.user.core.interfaces;

public class Projection {
    public Class<?> clazz;
    public String field;
    public String alias;

    public Projection(String alias, Class<?> fieldClass, String field) {
        this.clazz = fieldClass;
        this.field = field;
        this.alias=alias;
    }
}
