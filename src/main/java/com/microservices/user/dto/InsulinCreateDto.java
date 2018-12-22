package com.microservices.user.dto;

import java.util.Date;

import com.microservices.user.db.models.InsulineType;
import com.microservices.user.db.models.User;

public class InsulinCreateDto {
    public Long  id;
    public Integer dose;
    public InsulineType type;
    public String name;
    public User patient;
    public Date  datetime = new Date();
}
