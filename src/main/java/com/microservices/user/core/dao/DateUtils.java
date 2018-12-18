package com.microservices.user.core.dao;

import java.util.Date;

public class DateUtils {

    public static boolean isTimeUp(Date updateDate, Integer countDay){
        Date currentDate = new Date();
        Date upDate = org.apache.commons.lang3.time.DateUtils.addDays(updateDate, countDay);
        if(currentDate.compareTo(upDate) > 0){
            return true;
        }else{
            return false;
        }
    }
}
