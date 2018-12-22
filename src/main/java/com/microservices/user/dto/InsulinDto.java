package com.microservices.user.dto;

import java.util.Date;

import com.microservices.user.db.models.Insulin;
import com.microservices.user.db.models.InsulineType;
import com.microservices.user.db.models.User;

public class InsulinDto {
    public Long  id;
    public Integer dose;
    public InsulineType type;
    public String name;
    public User patient;
    public Date  datetime = new Date();
    
    public static InsulinDto create(Insulin insulin) {
        InsulinDto dto = new InsulinDto();
        dto.datetime = insulin.getDatetime();
        dto.id = insulin.getId();
        dto.dose = insulin.getDose();
        dto.name = insulin.getName();
        dto.type = insulin.getType();
        return dto;
    }
}
