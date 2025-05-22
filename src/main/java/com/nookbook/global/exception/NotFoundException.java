package com.nookbook.global.exception;

import com.nookbook.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
