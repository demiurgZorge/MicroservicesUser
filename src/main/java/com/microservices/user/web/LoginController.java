package com.microservices.user.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.user.core.apiresult.ApiResult;
import com.microservices.user.core.apiresult.ApiSuccessResult;
import com.microservices.user.core.apiresult.ApiErrorResult;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.servlet.HttpHeadersReader;
import com.microservices.user.dto.LoginDto;
import com.microservices.user.dto.UserDto;
import com.microservices.user.logic.UserLogic;

@RestController
@RequestMapping("/security")
public class LoginController {
    @Autowired
    HttpHeadersReader headersReader;
    
    @Autowired
    UserLogic userLogic;
    
    /**
     * # Запрос для создания пользователя 
     * POST http://{{host}}/auth/security/login
     * 
     * @return String
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<UserDto> login(HttpServletRequest request,
                                           @RequestBody(required = true) LoginDto loginDto) throws Exception {
        return ApiResult.success(userLogic.login(loginDto, headersReader.getHttpHeaders(request)));
    }
    
    public class TokenDto{
        public String token;
        publib TokenDto(){}
    }
    /**
     * Запрос для авторизации
     * POST http://{{host}}/auth/security/token
     * @param request
     * @param tokenDto
     * @return ApiSuccessResult<UserDto>
     */
    @RequestMapping(value="token", method=RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiSuccessResult<UserDto> loginWithToken(HttpServletRequest request, @RequestBody(required = true) TokenDto tokenDto) {        
        return ApiResult.success(userLogic.loginWithToken(tokenDto.token, headersReader.getHttpHeaders(request)));
    }
    
    
    /**
     * Запрос для выхода
     * GET http://{{host}}/auth/security/signout
     * @return RetValue<String>
     */
    @RequestMapping(value="/signout",method=RequestMethod.GET)
    public ApiSuccessResult<String> signOut(){
        return ApiResult.success(userLogic.signOut());
    }
    
    /**  Запрос для получения подробности залогиненного пользователя
     * GET http://{{host}}/auth/security/logged
     * @return RetValue<LoginResponseModel>
     */
    @RequestMapping(value="/logged", method=RequestMethod.GET)
    public ApiSuccessResult<UserDto> getLoggedUserDetail() {
        return ApiResult.success(userLogic.getLoggedUserDetail());
    }
    
    @ExceptionHandler(BaseException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResult handleBadRequest(BaseException exception) {
        return ApiResult.fail(exception);
    }
}
