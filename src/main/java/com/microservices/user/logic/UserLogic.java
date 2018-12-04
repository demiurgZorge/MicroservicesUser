package com.microservices.user.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservices.user.core.exceptions.BaseException;
import com.microservices.user.core.exceptions.ErrorCodeEnum;
import com.microservices.user.dao.UserDao;
import com.microservices.user.db.models.User;
import com.microservices.user.dto.CreateUserDto;
import com.microservices.user.dto.UserDto;

@Component
public class UserLogic {

	private static final Logger  logger = LoggerFactory.getLogger(UserLogic.class);
	enum Error implements ErrorCodeEnum{
		USER_NAME_IN_CREATE_DTO_IS_NULL("USER_NAME_IN_CREATE_DTO_IS_NULL"),
		CREATE_DTO_IS_NULL("CREATE_DTO_IS_NULL"),
		USER_WITH_ID_NOT_FOUND("USER_WITH_ID_NOT_FOUND");	
		
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
	UserDao userDao;
	
	public UserLogic(){
		super();
	}

	public UserDto getById(Long userId) {
		User user = userDao.getById(userId);
		if(user == null){
			throw BaseException.create(logger, Error.USER_WITH_ID_NOT_FOUND);
		}
		return UserDto.create(user);
	}

	public UserDto create(CreateUserDto userCreateDto) {
	    if (userCreateDto == null) {
            throw BaseException.create(logger, Error.CREATE_DTO_IS_NULL);
        }
	    
		if(userCreateDto.name == null){
			throw BaseException.create(logger, Error.USER_NAME_IN_CREATE_DTO_IS_NULL);
		}
		User user = userCreateDto.create();
		userDao.add(user);
		return UserDto.create(user);
	}
	
	
}
