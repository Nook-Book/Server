package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.AuthorizedException;

public class ChallengeNotAuthorizedException extends AuthorizedException {
    public ChallengeNotAuthorizedException() {
        super("C002", "챌린지에 대한 권한이 없습니다.");
    }
}
