package com.microservices.user.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.user.core.apiresult.ApiErrorResult;
import com.microservices.user.core.apiresult.ApiListResult;
import com.microservices.user.core.apiresult.ApiResult;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.dto.SugarCreateDto;
import com.microservices.user.dto.SugarDto;
import com.microservices.user.dto.SugarUpdateDto;
import com.microservices.user.logic.SugarLogic;

@RestController
@RequestMapping("/sugar")
public class SugarController {
    @Autowired
    SugarLogic sugarLogic;
    
    /**
     * ##### Запрос для создания уровня сахара POST http://{{host}}/auth/sugar/create/{userId}
     * 
     * @return String
     */
    @RequestMapping(value = "/create/{userId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SugarDto create(@RequestBody(required = true) SugarCreateDto sugarCreateDto, @PathVariable("userId") Long userId) throws Exception {
        return sugarLogic.create(sugarCreateDto.level, userId);
    }
    
    /**
     * ##### Запрос для обновления уровня сахара POST http://{{host}}/auth/sugar/update/{userId}
     * 
     * @return String
     */
    @RequestMapping(value = "/update/{userId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SugarDto update(@RequestBody(required = true) SugarUpdateDto sugarDto, @PathVariable("userId") Long userId) throws Exception {
        return sugarLogic.update(sugarDto, userId);
    }
    
    /**
     * ##### Запрос для получения уровня сахара GET http://{{host}}/auth/sugar/get/{sugarId}
     * 
     * @return String
     */
    @RequestMapping(value = "/get/{sugarId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public SugarDto getById(@PathVariable("sugarId") Long sugarId) throws Exception {
        return sugarLogic.getById(sugarId);
    }
    
    /**
     * ##### Запрос для получения списка сахара по двум датам GET http://{{host}}/auth/sugar/getrange/{userId}
     * 
     * @return String
     */
    @RequestMapping(value = "/getrange/{userId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiListResult<List<SugarDto>> list(@PathVariable("userId") Long userId,
                                         @RequestBody(required = false) QueryState query) throws Exception {
        return ApiResult.list(sugarLogic.listForUser(userId, query), sugarLogic.getRecordCount(userId, query)) ;
    }
    
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResult handleBadRequest(BaseException exception) {
        return ApiResult.fail(exception);
    }
}
