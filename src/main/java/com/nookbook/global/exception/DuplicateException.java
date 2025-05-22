package com.nookbook.global.exception;

import com.nookbook.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateException extends BusinessException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
