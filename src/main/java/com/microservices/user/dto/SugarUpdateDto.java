package com.microservices.user.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SugarUpdateDto {
    public Float level;
    public Date date;
    
    public SugarUpdateDto() {
        super();
    };
}
