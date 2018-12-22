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
import com.microservices.user.dto.InsulinCreateDto;
import com.microservices.user.dto.InsulinDto;
import com.microservices.user.logic.InsulinLogic;

@RestController
@RequestMapping("/insulin")
public class InsulinController {
    @Autowired
    InsulinLogic insulinLogic;
    
    /**
     * ##### Запрос для создания уровня введенного инсулина 
     * POST http://{{host}}/auth/insulin/create
     * 
     * @return String
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<InsulinDto> create(@RequestBody(required = true) InsulinCreateDto insulinCreateDto) throws Exception {
        return ApiResult.success(insulinLogic.create(insulinCreateDto));
    }
    
    /**
     * ##### Запрос для обновления уровня введенного инсулина 
     * POST http://{{host}}/auth/insulin/update/{insulinId}
     * 
     * @return String
     */
    @RequestMapping(value = "/update/{insulinId}", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<InsulinDto> update(@RequestBody(required = true) InsulinCreateDto insulinCreateDto, 
                                             @PathVariable("insulinId") Long insulinId) throws Exception {
        return ApiResult.success(insulinLogic.update(insulinCreateDto, insulinId));
    }
    
    /**
     * ##### Запрос для получения уровня введенного инсулина по двум датам 
     * POST http://{{host}}/auth/insulin/getrange
     * 
     * @return String
     */
    @RequestMapping(value = "/getrange", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiListResult<List<InsulinDto>> list(@RequestBody(required = false) QueryState query) throws Exception {
        return ApiResult.list(insulinLogic.listForUser(query), insulinLogic.getRecordCount(query)) ;
    }
    
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResult handleBadRequest(BaseException exception) {
        return ApiResult.fail(exception);
    }
}
