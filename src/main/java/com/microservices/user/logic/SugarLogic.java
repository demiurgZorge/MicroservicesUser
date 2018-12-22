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
import com.microservices.user.dao.SugarDao;
import com.microservices.user.dao.SugarQuerySpecification;
import com.microservices.user.db.models.Sugar;
import com.microservices.user.db.models.User;
import com.microservices.user.dto.DeleteSugarDto;
import com.microservices.user.dto.SugarDto;
import com.microservices.user.dto.SugarUpdateDto;


@Component
public class SugarLogic {
    private static final double MAX_SUGAR_LEVEL = 100.0;
    private static final double MIN_SUGAR_LEVEL = 0.0;
    private static final Logger logger = LoggerFactory.getLogger(SugarLogic.class);
    enum Error implements ErrorCodeEnum {
        SUGAR_OUT_OF_RANGE("SUGAR_EXIDE_RANGE"),
        SUGAR_IS_NULL("SUGAR_IS_NULL"),
        UPDATE_DTO_IS_NULL("UPDATE_DTO_IS_NULL"),
        SUGAR_WITH_ID_NOT_FOUND("SUGAR_WITH_ID_NOT_FOUND"), 
        USER_HAS_NO_RIGHT("USER_HAS_NO_RIGHT");
        
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
    
    @Autowired
    UserSessionLogic userSessionLogic;
    
    public SugarLogic() {
        super();
    }
    
    public SugarDto getById(Long sugarId) {
        Sugar sugar = sugarDao.getById(sugarId);
        if (sugar == null) {
            throw BaseException.create(logger, Error.SUGAR_WITH_ID_NOT_FOUND);
        }
        hasUserRight(sugar.getPatient().getId());
        return SugarDto.create(sugar);
    }
    
    public SugarDto update(SugarUpdateDto updateDto, Long sugarId) {
        if (updateDto == null) {
            throw BaseException.create(logger, Error.UPDATE_DTO_IS_NULL);
        }
        Sugar sugar = sugarDao.getById(sugarId);
        if (sugar == null) {
            throw BaseException.create(logger, Error.SUGAR_WITH_ID_NOT_FOUND);
        }
        hasUserRight(sugar.getPatient().getId());
        if (updateDto.level != null) {
            if (updateDto.level <= MIN_SUGAR_LEVEL || updateDto.level > MAX_SUGAR_LEVEL) {
                throw BaseException.create(logger, Error.SUGAR_OUT_OF_RANGE);
            }
            sugar.setLevel(updateDto.level);
            
        }
        if (updateDto.date != null) {
            sugar.setDatetime(updateDto.date);            
        }
        sugarDao.update(sugar);
        return SugarDto.create(sugar);
    }
    
    public List<SugarDto> listForUser(QueryState query){
        Long userId = userSessionLogic.getCurrentUserId();
        SugarQuerySpecification spec = new SugarQuerySpecification(query);
        spec.setFilterValue(SugarQuerySpecification.Filters.patientId, userId);
        List<Sugar> list = sugarDao.query(spec);        
        return SugarDto.list(list);
    }

    private void hasUserRight(Long userId) {
        if(!userSessionLogic.isUserLogged(userId)) {
            throw BaseException.create(logger, Error.USER_HAS_NO_RIGHT);
        }
    }
    
    public SugarDto create(SugarUpdateDto dto) {
        if (dto == null) {
            throw BaseException.create(logger, Error.UPDATE_DTO_IS_NULL);
        }
        if (dto.level == null) {
            throw BaseException.create(logger, Error.SUGAR_IS_NULL);
        }
        
        if (dto.level <= MIN_SUGAR_LEVEL || dto.level > MAX_SUGAR_LEVEL) {
            throw BaseException.create(logger, Error.SUGAR_OUT_OF_RANGE);
        }
        Long userId = userSessionLogic.getCurrentUserId();
        User user = userLogic.getById(userId );
        Sugar sugar = new Sugar(dto.level, user);
        Date date = dto.date;
        if(date == null) {
            date = new Date();
        }
        sugar.setDatetime(date);
        sugarDao.add(sugar);
        return SugarDto.create(sugar);
    }
    public QueryMetaInformation getRecordCount(QueryState queryState) {
        Long userId = userSessionLogic.getCurrentUserId();
        SugarQuerySpecification spec = new SugarQuerySpecification(queryState);
        spec.setFilterValue(SugarQuerySpecification.Filters.patientId, userId);
        long count = sugarDao.getRecordCount(spec);
        return new QueryMetaInformation(queryState, count);
    }

    public Boolean deleteByListId(DeleteSugarDto dto) {
        Long userId = userSessionLogic.getCurrentUserId();
        if(dto.idList == null) {
            return true;
        }
        SugarQuerySpecification spec = new SugarQuerySpecification(null);
        spec.setFilterValue(SugarQuerySpecification.Filters.patientId, userId);
        spec.setFilterValue(SugarQuerySpecification.Filters.idList, dto.idList);
        List<Sugar> list = sugarDao.query(spec);
        
        List<Long> userSugerIdList = Sugar.extractIdList(list);
        if(userSugerIdList.isEmpty()) {
            return true;
        }
        sugarDao.deleteByIdList(userSugerIdList);
        return true;
    }

}
