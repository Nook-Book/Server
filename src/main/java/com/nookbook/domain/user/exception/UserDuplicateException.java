package com.nookbook.domain.user.exception;

import com.nookbook.global.exception.DuplicateException;

public class UserDuplicateException extends DuplicateException {
    public UserDuplicateException() {
        super("U002", "이미 존재하는 사용자입니다."); // U002 is a custom code for user duplication
    }
}
