package com.nookbook.domain.verification.exception;

import com.nookbook.global.exception.BusinessException;
import com.nookbook.global.payload.ErrorCode;

public class VerificationCodeMismatchException extends BusinessException {
    public VerificationCodeMismatchException() {
        super(ErrorCode.VERIFICATION_CODE_MISMATCH);
    }
}
