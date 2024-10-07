package com.nookbook.global.exception;

import lombok.Getter;

@Getter
public class AuthorizedException extends RuntimeException{
    private final int statusCode;
    private final String code;
    private final String message;

    public AuthorizedException(String code, String message) {
        super(message);
        this.statusCode = 403;
        this.code = code;
        this.message = message;
    }
}
