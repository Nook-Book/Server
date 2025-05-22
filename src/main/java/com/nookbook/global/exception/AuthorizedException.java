package com.nookbook.global.exception;

import com.nookbook.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizedException extends BusinessException {
    public AuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
