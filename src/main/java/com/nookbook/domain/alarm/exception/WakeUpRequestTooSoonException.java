package com.nookbook.domain.alarm.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class WakeUpRequestTooSoonException extends BusinessException {
    public WakeUpRequestTooSoonException() {
        super(ErrorCode.WAKE_UP_REQUEST_TOO_SOON);
    }
}
