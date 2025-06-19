package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class SelfWakeUpNotAllowedException extends BusinessException {
    public SelfWakeUpNotAllowedException() {
        super(ErrorCode.SELF_WAKE_UP_NOT_ALLOWED);
    }
}