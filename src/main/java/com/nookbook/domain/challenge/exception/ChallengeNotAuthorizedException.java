package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.AuthorizedException;
import com.nookbook.global.payload.ErrorCode;

public class ChallengeNotAuthorizedException extends AuthorizedException {
    public ChallengeNotAuthorizedException() {
        super(ErrorCode.CHALLENGE_NOT_AUTHORIZED);
    }
}
