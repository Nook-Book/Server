package com.nookbook.domain.challenge.exception;

import com.nookbook.global.exception.NotFoundException;
import com.nookbook.global.payload.ErrorCode;

public class ParticipantNotFoundException extends NotFoundException {
        public ParticipantNotFoundException() {
            super(ErrorCode.PARTICIPANT_NOT_FOUND);
        }
}
