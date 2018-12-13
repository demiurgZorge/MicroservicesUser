package com.microservices.user.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.microservices.user.dto.SugarCreateDto;
import com.microservices.user.dto.SugarDto;
import com.microservices.user.logic.SugarLogic;

@RestController
@RequestMapping("/sugar")
public class SugarController {
    @Autowired
    SugarLogic sugarLogic;
    
    /**
     * ##### Запрос для создания уровня сахара GET http://{{host}}/auth/sugar/create
     * 
     * @return String
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public SugarDto create(@RequestBody(required = true) SugarCreateDto sugarCreateDto) throws Exception {
        return sugarLogic.create(sugarCreateDto.level);
    }
    
    /**
     * ##### Запрос для получения списка сахара по двум датам GET http://{{host}}/auth/sugar/getrange
     * 
     * @return String
     */
    @RequestMapping(value = "/getrange", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public List<SugarDto> getRangeByDate() throws Exception {
        return sugarLogic.getRangeByDate(null, null);
    }
}
