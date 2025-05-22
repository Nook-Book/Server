package com.nookbook.domain.auth.exception;

import com.nookbook.global.exception.AuthenticationException;
import com.nookbook.global.payload.ErrorCode;

public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
