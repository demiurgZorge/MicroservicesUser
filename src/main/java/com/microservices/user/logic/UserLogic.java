package com.microservices.user.logic;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.microservices.user.core.crypto.PasswordValidator;
import com.microservices.user.core.crypto.UserAuthTokenGeneratorService;
import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;
import com.microservices.user.core.servlet.HttpHeadersReader.LogHeaders;
import com.microservices.user.core.session.UserSessionLogic;
import com.microservices.user.core.session.UserSessionLogic.UserSessionDto;
import com.microservices.user.dao.UserDao;
import com.microservices.user.db.models.User;
import com.microservices.user.dto.CreateUserDto;
import com.microservices.user.dto.LoginDto;
import com.microservices.user.dto.UserDto;

@Component
public class UserLogic {
    
    private static final Logger logger = LoggerFactory.getLogger(UserLogic.class);
    
    enum Error implements ErrorCodeEnum {
        USER_NAME_IN_CREATE_DTO_IS_NULL("USER_NAME_IN_CREATE_DTO_IS_NULL"),
        USER_LOGIN_IN_CREATE_DTO_IS_NULL("USER_LOGIN_IN_CREATE_DTO_IS_NULL"), 
        CREATE_DTO_IS_NULL("CREATE_DTO_IS_NULL"), 
        USER_WITH_LOGIN_NOT_FOUND("USER_WITH_LOGIN_NOT_FOUND"),
        USER_WITH_LOGIN_ALREDY_EXISTS("USER_WITH_LOGIN_ALREDY_EXISTS"),
        USER_WITH_LOGIN_NOT_ACTIVE("USER_WITH_LOGIN_NOT_ACTIVE"),
        USER_PASSWORD_IS_NULL("USER_PASSWORD_IS_NULL"),
        USER_PASSWORD_IS_WRONG("USER_PASSWORD_IS_WRONG"),
        USER_WITH_ID_NOT_FOUND("USER_WITH_ID_NOT_FOUND"), 
        USER_UNAUTHORIZED("USER_UNAUTHORIZED"), 
        INVALID_TOKEN("INVALID_TOKEN"), 
        USER_NOT_REGISTERED("");
        
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
    
    @Autowired
    UserSessionLogic userSessionLogic;
    
    @Autowired
    PasswordValidator passwordValidator;
    
    @Autowired
    private UserAuthTokenGeneratorService authTokenGenerator;
    
    @Autowired(required = true)
    private HttpServletResponse response;
    
    public UserLogic() {
        super();
    }
    
    public User getById(Long userId) {
        User user = userDao.getById(userId);
        if (user == null) {
            throw BaseException.create(logger, Error.USER_WITH_ID_NOT_FOUND);
        }
        return user;
    }
    
    public UserDto getDtoById(Long userId) {
        return UserDto.create(getById(userId));
    }
    
    public UserDto registerUser(CreateUserDto userCreateDto, Map<String, Object> httpHeaders) {
        if (userCreateDto == null) {
            throw BaseException.create(logger, Error.CREATE_DTO_IS_NULL);
        }
        
        if (userCreateDto.name == null) {
            throw BaseException.create(logger, Error.USER_NAME_IN_CREATE_DTO_IS_NULL);
        }
        if (userCreateDto.login == null) {
            throw BaseException.create(logger, Error.USER_LOGIN_IN_CREATE_DTO_IS_NULL);
        }
        if (userCreateDto.password == null) {
            throw BaseException.create(logger, Error.USER_PASSWORD_IS_NULL);
        }
        User user = userCreateDto.create();
        vaildateLogin(user.getLogin());
        passwordValidator.validate(userCreateDto.password);
        user.setNewPassword(userCreateDto.password );
        userDao.add(user);
        UserSessionDto sessionDto = userSessionLogic.saveCurrentUser(user, httpHeaders);
        
        return UserDto.createWithToken(user, sessionDto.getAuthToken());
    }

    private void vaildateLogin(String login) {
        List<User> userList =  userDao.getByField("login", login);
        if(!userList.isEmpty()) {
            throw BaseException.create(logger, Error.USER_WITH_LOGIN_ALREDY_EXISTS);
        }
    }

    public UserDto login(LoginDto loginDto, Map<String, Object> httpHeaders) {
        List<User> userList =  userDao.getByField("login", loginDto.login);
        if(userList.isEmpty()) {
            throw BaseException.create(logger, Error.USER_WITH_LOGIN_NOT_FOUND);
        }
        User user = userList.get(0);
        checkUserCouldLogin(user);
        if(!user.isValidPassword(loginDto.password)) {
            throw BaseException.create(logger, Error.USER_PASSWORD_IS_WRONG);
        }
        UserSessionDto userDto = userSessionLogic.saveCurrentUser(user, httpHeaders);
        return UserDto.createWithToken(user, userDto.getAuthToken());
    }
    
    public String signOut() {
            UserSessionDto userModel = userSessionLogic.getCurrentUser();

            if(userModel != null){
                userSessionLogic.signOut(response);
                return "logout";
            }
            else {
                throw new BaseException(Error.USER_UNAUTHORIZED);
            }

    }

    public UserDto loginWithToken(String token, Map<String, Object> httpHeaders) {
        User user = loginUser(token);
        userSessionLogic.saveCurrentUser(user, token, httpHeaders);
        updateUser(user, httpHeaders);
        UserSessionDto userDto = userSessionLogic.saveCurrentUser(user, httpHeaders);
        return UserDto.createWithToken(user, userDto.getAuthToken());
    }
    
    private User loginUser (String token) {
        Long userId = authTokenGenerator.getUserId(token);
        if ( userId == null) {
            throw new BaseException(Error.INVALID_TOKEN);
        }
        
        User user = userDao.getById(userId);

        if (user == null ) {
            throw new BaseException(Error.USER_NOT_REGISTERED);
        }
        
        checkUserCouldLogin(user);
        return user;
    }
    
    private void checkUserCouldLogin(User user) {
        if(!user.getActive()) {
            throw BaseException.create(logger, Error.USER_WITH_LOGIN_NOT_ACTIVE);
        }
    }

    protected void updateUser(User user, Map<String, Object> httpHeaders) {
        String userIP = "";
        if (httpHeaders.get(LogHeaders.NGINX_FWD.toString()) != null) {
            userIP = httpHeaders.get(LogHeaders.NGINX_FWD.toString()).toString();
        }
        if (userIP.isEmpty() && httpHeaders.get("IP") != null) {
            userIP = httpHeaders.get("IP").toString();
        }
        user.setIp(userIP);
        userDao.update(user);
    }

    public UserDto getLoggedUserDetail() {
        UserSessionDto sessionDto = userSessionLogic.getCurrentUser();
        User user = userDao.getById(sessionDto.getUserId());        
        return UserDto.createWithToken(user, sessionDto.getAuthToken());
    }

}
