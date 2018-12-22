package com.microservices.user.logic;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.QueryMetaInformation;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;
import com.microservices.user.core.session.UserSessionLogic;
import com.microservices.user.dao.InsulinDao;
import com.microservices.user.db.models.Insulin;
import com.microservices.user.db.models.User;
import com.microservices.user.dto.InsulinCreateDto;
import com.microservices.user.dto.InsulinDto;

@Component
public class InsulinLogic {
    private static final double MAX_INSULIN_DOSE = 100.0;
    private static final double MIN_INSULIN_DOSE = 0.0;

    private static final Logger logger = LoggerFactory.getLogger(InsulinLogic.class);
    enum Error implements ErrorCodeEnum {
        INSULINE_DOSE_OUT_OF_RANGE("INSULINE_DOSE_OUT_OF_RANGE"), 
        UPDATE_DTO_IS_NULL("UPDATE_DTO_IS_NULL"), 
        DOSE_IS_NULL("DOSE_IS_NULL"), 
        DOSE_OUT_OF_RANGE("DOSE_OUT_OF_RANGE"), TYPE_IS_NULL("TYPE_IS_NULL");
        
        private final String text;
        
        private Error(final String text) {
            this.text = text;
        }
        
        @Override
        public String toString() {
            return text;
        }
        
        @Override
        public String code() {
            return text;
        }
    }
    public InsulinLogic() {
        super();
    }
    
    @Autowired
    UserLogic userLogic;
    
    @Autowired
    InsulinDao insulinDao;
    
    @Autowired
    UserSessionLogic sessionLogic;

    public InsulinDto create(InsulinCreateDto dto) {
        if (dto == null) {
            throw BaseException.create(logger, Error.UPDATE_DTO_IS_NULL);
        }
        if (dto.dose == null) {
            throw BaseException.create(logger, Error.DOSE_IS_NULL);
        }
        
        if (dto.dose <= MIN_INSULIN_DOSE || dto.dose > MAX_INSULIN_DOSE) {
            throw BaseException.create(logger, Error.DOSE_OUT_OF_RANGE);
        }
        if (dto.type == null) {
            throw BaseException.create(logger, Error.TYPE_IS_NULL);
        }
        Long userId = sessionLogic.getCurrentUserId();
        User user = userLogic.getById(userId);
        Date date = dto.datetime;
        if(date == null) {
            date = new Date();
        }
        Insulin insulin = new  Insulin();
        insulin.setDatetime(date);
        insulin.setDose(dto.dose);
        insulin.setName(dto.name);
        insulin.setType(dto.type);
        insulin.setPatient(user);
        insulinDao.add(insulin);
        return InsulinDto.create(insulin);
    }

    public InsulinDto update(InsulinCreateDto updateDto, Long insulinId) {
        Long userId = sessionLogic.getCurrentUserId();
        if (updateDto == null) {
            throw BaseException.create(logger, Error.UPDATE_DTO_IS_NULL);
        }
        return null;
    }

    public List<InsulinDto> listForUser(QueryState query) {
        Long userId = sessionLogic.getCurrentUserId();
        return null;
    }

    public QueryMetaInformation getRecordCount(QueryState query) {
        Long userId = sessionLogic.getCurrentUserId();
        return null;
    }
}
