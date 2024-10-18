package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.NotFoundException;

public class ChallengeNotFoundException extends NotFoundException {
    public ChallengeNotFoundException() {
        super("C001", "챌린지를 찾을 수 없습니다.");
    }
}
