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
import com.microservices.user.core.apiresult.ApiSuccessResult;
import com.microservices.user.core.dao.QueryState;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.dto.DeleteSugarDto;
import com.microservices.user.dto.SugarDto;
import com.microservices.user.dto.SugarUpdateDto;
import com.microservices.user.logic.SugarLogic;

@RestController
@RequestMapping("/sugar")
public class SugarController {
    @Autowired
    SugarLogic sugarLogic;
    
    /**
     * ##### Запрос для создания уровня сахара 
     * POST http://{{host}}/auth/sugar/create
     * 
     * @return String
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<SugarDto> create(@RequestBody(required = true) SugarUpdateDto sugarCreateDto) throws Exception {
        return ApiResult.success(sugarLogic.create(sugarCreateDto));
    }
    
    /**
     * ##### Запрос для обновления уровня сахара 
     * POST http://{{host}}/auth/sugar/update/{sugarId}
     * 
     * @return String
     */
    @RequestMapping(value = "/update/{sugarId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<SugarDto> update(@RequestBody(required = true) SugarUpdateDto sugarDto, 
                                             @PathVariable("sugarId") Long sugarId) throws Exception {
        return ApiResult.success(sugarLogic.update(sugarDto, sugarId));
    }
    
    /**
     * ##### Запрос для удаления уровня сахара 
     * POST http://{{host}}/auth/sugar/delete
     * 
     * @return String
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<Boolean> deleteByListId(@RequestBody(required = true) DeleteSugarDto deleteSugarDto) throws Exception {
        return ApiResult.success(sugarLogic.deleteByListId(deleteSugarDto));
    }

    
    /**
     * ##### Запрос для получения уровня сахара 
     * GET http://{{host}}/auth/sugar/get/{sugarId}
     * 
     * @return String
     */
    @RequestMapping(value = "/get/{sugarId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<SugarDto> getById(@PathVariable("sugarId") Long sugarId) throws Exception {
        return ApiResult.success(sugarLogic.getById(sugarId));
    }
    
    /**
     * ##### Запрос для получения списка сахара по двум датам 
     * POST http://{{host}}/auth/sugar/getrange
     * 
     * @return String
     */
    @RequestMapping(value = "/getrange", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiListResult<List<SugarDto>> list(@RequestBody(required = false) QueryState query) throws Exception {
        return ApiResult.list(sugarLogic.listForUser(query), sugarLogic.getRecordCount(query)) ;
    }
    
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResult handleBadRequest(BaseException exception) {
        return ApiResult.fail(exception);
    }
}
