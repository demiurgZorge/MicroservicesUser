package com.microservices.user.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.microservices.user.db.models.Sugar;

public class SugarDto {
    public Long   id;
    public Float level;
    public Date date;
    public UserDto userDto;
    
    public SugarDto() {
        super();
    };
    
    public static SugarDto create(Sugar sugar) {
        SugarDto dto = new SugarDto();
        dto.id = sugar.getId();
        dto.date = sugar.getDatetime();
        dto.level = sugar.getLevel();
//        if (sugar.getUser() != null) {
//            dto.userDto = UserDto.create(sugar.getUser());
//        }
        return dto;
    }
    
    public static List<SugarDto> list(List<Sugar> sugarList){
        List<SugarDto> list = new ArrayList<>();
        for (Sugar sugar : sugarList) {
            list.add(SugarDto.create(sugar));
        }
        return list;
    }
}
