package com.nookbook.domain.user.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

}
