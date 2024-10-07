package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.NotFoundException;

public class ParticipantNotFoundException extends NotFoundException {
        public ParticipantNotFoundException() {
            super("P001", "참가자를 찾을 수 없습니다.");
        }
}
