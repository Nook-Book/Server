package com.nookbook.global.exception;

import com.nookbook.global.payload.ErrorCode;

public class AlreadyExistsException extends BusinessException {
    public AlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
