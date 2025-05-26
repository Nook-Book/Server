package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class ChallengeNotFoundException extends NotFoundException {
    public ChallengeNotFoundException() {
        super(ErrorCode.CHALLENGE_NOT_AUTHORIZED);
    }
}
