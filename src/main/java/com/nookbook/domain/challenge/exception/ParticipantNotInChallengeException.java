package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.AuthorizedException;
import com.nookbook.global.payload.ErrorCode;

public class ParticipantNotInChallengeException extends AuthorizedException {
    public ParticipantNotInChallengeException() {
        super(ErrorCode.PARTICIPANT_NOT_IN_CHALLENGE);
    }
}
