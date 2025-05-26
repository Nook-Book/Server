package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.AuthorizedException;
import com.nookbook.global.payload.ErrorCode;

public class ChallengeOwnerCantLeaveException extends AuthorizedException {
    public ChallengeOwnerCantLeaveException() {
        super(ErrorCode.CHALLENGE_OWNER_CANT_LEAVE);
    }
}
