package com.nookbook.domain.verification.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class VerificationNotCompletedException extends BusinessException {
    public VerificationNotCompletedException() {
        super(ErrorCode.VERIFICATION_NOT_COMPLETED);
    }
}
