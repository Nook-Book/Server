package com.nookbook.domain.user.exception;

import com.nookbook.global.exception.DuplicateException;
import com.nookbook.global.payload.ErrorCode;

public class UserDuplicateException extends DuplicateException {
    public UserDuplicateException() {
        super(ErrorCode.USER_DUPLICATE);
    }
}
