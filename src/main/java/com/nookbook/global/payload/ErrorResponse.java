package com.nookbook.global.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ErrorResponse {
    private String message;
    private String code;
    private ErrorResponse(final ErrorCode code) {
        this.message = code.getMessage();
        this.code = code.getCode();
    }
    public ErrorResponse(final ErrorCode code, final String message) {
        this.message = message;
        this.code = code.getCode();

    }
    public static ErrorResponse of(final ErrorCode code, final String message) {
        return new ErrorResponse(code, message);

    }
}