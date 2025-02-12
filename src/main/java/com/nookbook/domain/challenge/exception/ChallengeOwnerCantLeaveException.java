package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.AuthorizedException;

public class ChallengeOwnerCantLeaveException extends AuthorizedException {
    public ChallengeOwnerCantLeaveException() {
        super("C003", "방장은 챌린지에서 나갈 수 없습니다.");
    }
}
