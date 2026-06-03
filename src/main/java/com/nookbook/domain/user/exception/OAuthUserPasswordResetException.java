package com.nookbook.domain.user.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class OAuthUserPasswordResetException extends BusinessException {
    public OAuthUserPasswordResetException() {
        super(ErrorCode.OAUTH_USER_PASSWORD_RESET);
    }
}
