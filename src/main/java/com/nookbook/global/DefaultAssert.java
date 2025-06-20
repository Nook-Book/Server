package com.nookbook.global;

import com.nookbook.global.exception.AuthenticationException;
import com.nookbook.global.exception.DefaultException;
import com.nookbook.global.payload.ErrorCode;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

public class DefaultAssert extends Assert {

    public static void isTrue(boolean value){
        if(!value){
            throw new DefaultException(ErrorCode.INVALID_CHECK);
        }
    }

    public static void isTrue(boolean value, String message){
        if(!value){
            throw new DefaultException(ErrorCode.INVALID_CHECK, message);
        }
    }


    public static void isListNull(List<Object> values){
        if(values.isEmpty()){
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH);
        }
    }

    public static void isListNull(Object[] values){
        if(values == null){
            throw new DefaultException(ErrorCode.INVALID_FILE_PATH);
        }
    }

    public static void isOptionalPresent(Optional<?> value){
        if(!value.isPresent()){
            throw new DefaultException(ErrorCode.INVALID_PARAMETER);
        }
    }

    public static void isAuthentication(String message){
        throw new AuthenticationException(message);
    }

    public static void isAuthentication(boolean value){
        if(!value){
            throw new AuthenticationException(ErrorCode.INVALID_AUTHENTICATION);
        }
    }


}