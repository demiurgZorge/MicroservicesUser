package com.microservices.user.logic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.QueryMetaInformation;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;
import com.microservices.user.dao.SugarDao;
import com.microservices.user.dao.SugarQuerySpecification;
import com.microservices.user.db.models.Sugar;
import com.microservices.user.db.models.User;
import com.microservices.user.dto.SugarDto;


@Component
public class SugarLogic {
    private static final Logger logger = LoggerFactory.getLogger(SugarLogic.class);
    enum Error implements ErrorCodeEnum {
        SUGAR_OUT_OF_RANGE("SUGAR_EXIDE_RANGE"),
        SUGAR_IS_NULL("SUGAR_IS_NULL"),
        SUGAR_WITH_ID_NOT_FOUND("SUGAR_WITH_ID_NOT_FOUND");
        
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
    @Autowired
    UserLogic userLogic;
    
    @Autowired
    SugarDao  sugarDao;
    
    public SugarLogic() {
        super();
    }
    
    public SugarDto getById(Long sugarId) {
        Sugar sugar = sugarDao.getById(sugarId);
        if (sugar == null) {
            throw BaseException.create(logger, Error.SUGAR_WITH_ID_NOT_FOUND);
        }
        return SugarDto.create(sugar);
    }
    
    public SugarDto update(Float sugarLevel, Long sugarId) {
        Sugar sugar = sugarDao.getById(sugarId);
        if (sugar == null) {
            throw BaseException.create(logger, Error.SUGAR_WITH_ID_NOT_FOUND);
        }
        sugar.setLevel(sugarLevel);
        sugarDao.update(sugar);
        return SugarDto.create(sugar);
    }
    
    public List<SugarDto> listForUser(Long userId, QueryState query){
        SugarQuerySpecification spec = new SugarQuerySpecification(query);
        spec.setFilterValue(SugarQuerySpecification.Filters.patientId, userId);
        List<Sugar> list = sugarDao.query(spec);        
        return SugarDto.list(list);
    }
    
    public SugarDto create(Float sugarLevel, Long userId) {
        if (sugarLevel == null) {
            throw BaseException.create(logger, Error.SUGAR_IS_NULL);
        }
        
        if (sugarLevel <= 0.0 || sugarLevel > 100.0) {
            throw BaseException.create(logger, Error.SUGAR_OUT_OF_RANGE);
        }
        User user = userLogic.getById(userId);
        Sugar sugar = new Sugar(sugarLevel, user);
        sugarDao.add(sugar);
        return SugarDto.create(sugar);
    }
    public QueryMetaInformation getRecordCount(Long userId, QueryState queryState) {
        SugarQuerySpecification spec = new SugarQuerySpecification(queryState);
        spec.setFilterValue(SugarQuerySpecification.Filters.patientId, userId);
        long count = sugarDao.getRecordCount(spec);
        return new QueryMetaInformation(queryState, count);
    }

}
