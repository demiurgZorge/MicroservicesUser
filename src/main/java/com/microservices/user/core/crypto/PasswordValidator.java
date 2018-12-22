package com.microservices.user.core.crypto;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.microservices.user.core.dao.exceptions.BaseException;
import com.microservices.user.core.dao.exceptions.ErrorCodeEnum;

@Component
public class PasswordValidator {

        public enum Error implements ErrorCodeEnum {
            PASSWORD_MUST_CONTAIN_DIGIT("Password must contain digit"),
            PASSWORD_MUST_CONTAIN_UPPER_LETTER("Password must contain a upper letter"),
            PASSWORD_MUST_CONTAIN_LOWER_LETTER("Password must contain a lower letter"),
            PASSWORD_MUST_BE_AT_LEST_8("Password length must be at least 8 symbol"),
            PASSWORD_MUST_BE_NOT_EMPTY("Password must be not empty"),
            PASSWORD_IS_NOT_CORRECT("Password is not correct");

            private final String text;
            private Error(final String text) {
                this.text = text;
            }
            @Override
            public String code() {
                return this.name();
            }
            @Override
            public String toString() {
                return text;
            }
        }

        //private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);

        private Pattern pattern;
        private Matcher matcher;

        private static final String PASSWORD_PATTERN =
                "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

        public PasswordValidator(){
            pattern = Pattern.compile(PASSWORD_PATTERN);
        }

        public void validate(final String password){

            if(password == null){
                throw new BaseException(Error.PASSWORD_IS_NOT_CORRECT);
//                throw DetailedException.createWithDto(logger,
//                        Error.PASSWORD_IS_NOT_CORRECT, getIncorrectRuleCodeErrors(password));
            }

            matcher = pattern.matcher(password);
            if(!matcher.matches()){
                throw new BaseException(Error.PASSWORD_IS_NOT_CORRECT);
//                throw DetailedException.createWithDto(logger,
//                        Error.PASSWORD_IS_NOT_CORRECT, getIncorrectRuleCodeErrors(password));
            }
        }

        public List<ErrorCodeEnum> getIncorrectRuleCodeErrors(String password){
            List<ErrorCodeEnum> enums = new ArrayList<>();
            if(password == null){
                enums.add(Error.PASSWORD_MUST_BE_NOT_EMPTY);
            }else {

                if (!password.matches(".*\\d+.*")) {
                    enums.add(Error.PASSWORD_MUST_CONTAIN_DIGIT);
                }

                if (!password.matches(".*[A-Z].*")) {
                    enums.add(Error.PASSWORD_MUST_CONTAIN_UPPER_LETTER);
                }

                if (!password.matches(".*[a-z].*")) {
                    enums.add(Error.PASSWORD_MUST_CONTAIN_LOWER_LETTER);
                }

                if (password.length() < 8) {
                    enums.add(Error.PASSWORD_MUST_BE_AT_LEST_8);
                }
            }

            return enums;
        }

}
