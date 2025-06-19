package com.nookbook.domain.alarm.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class PushNotificationFailedException extends BusinessException {
    public PushNotificationFailedException() {
        super(ErrorCode.ALARM_PUSH_SEND_FAILED);
    }
}
