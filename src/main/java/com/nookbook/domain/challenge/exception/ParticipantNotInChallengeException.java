package com.nookbook.domain.challenge.exception;

public class ParticipantNotInChallengeException extends RuntimeException {
    private final String code;
    private final int statusCode;

    public ParticipantNotInChallengeException() {
        super("참가자가 챌린지에 속해있지 않습니다.");
        this.code = "C003";  // String 형태의 코드
        this.statusCode = 403;  // 상태 코드
    }

    public String getCode() {
        return code;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
