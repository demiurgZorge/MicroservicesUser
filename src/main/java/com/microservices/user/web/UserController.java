package com.microservices.user.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.user.dto.CreateUserDto;
import com.microservices.user.dto.UserDto;
import com.microservices.user.logic.UserLogic;

/**
 * ### UserController.
 *
 * Registration and auth
 * 
 * @author Azhuravl
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    UserLogic userLogic;
    
    /**
     * ##### Запрос для test GET http://{{host}}/auth/user/test
     * 
     * @return String
     */
    @RequestMapping(value = "test", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String test() throws Exception {
        return "UserController test";
    }
    
    /**
     * ##### Запрос для test GET http://{{host}}/auth/user/get/{userId}
     * 
     * @return String
     */
    @RequestMapping(value = "get/{userId}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public UserDto getById(@PathVariable("userId") Long userId) throws Exception {
        return userLogic.getById(userId);
    }
    
    /**
     * ##### Запрос для создания пользователя POST http://{{host}}/auth/user/create
     * 
     * @return String
     */
    @RequestMapping(value = "create", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public UserDto getByid(@RequestBody(required = true) CreateUserDto userCreateDto) throws Exception {
        return userLogic.create(userCreateDto);
    }
}
