package com.nookbook.domain.alarm.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class ExpoTokenNotFoundException extends NotFoundException {
    public ExpoTokenNotFoundException() {
        super(ErrorCode.EXPO_TOKEN_NOT_FOUND);
    }
}
