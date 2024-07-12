package com.nookbook.global.payload;



import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CustomResponse<T>{

    private T data;
    private LocalDateTime transaction_time;
    private HttpStatus status;
    private String description;
    private int statusCode;

    @Builder
    public CustomResponse(T data, LocalDateTime transaction_time, HttpStatus status, String description, int statusCode) {
        this.data = data;
        this.transaction_time = transaction_time;
        this.status = status;
        this.description = description;
        this.statusCode = statusCode;
    }

    // OK
    public static <T> CustomResponse<T> CREATED(@Nullable T data) {
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.CREATED)
                .statusCode(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> CustomResponse<T> OK(@Nullable T data) {
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .data(data)
                .build();
    }

    public static <T> CustomResponse<T> OK() {
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.OK)
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    public static <T> CustomResponse<T> BAD_REQUEST(@Nullable String description){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .description(description)
                .build();
    }

    public static <T> CustomResponse<T> BAD_REQUEST(@Nullable T data){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST)
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .data(data)
                .build();
    }

    public static <T> CustomResponse<T> NOT_FOUND(@Nullable T data){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .data(data)
                .build();
    }

    public static <T> CustomResponse<T> NOT_FOUND(@Nullable String description){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .description(description)
                .build();
    }

    public static <T> CustomResponse<T> FORBIDDEN(){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .build();
    }

    public static <T> CustomResponse<T> FORBIDDEN(String description){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN)
                .statusCode(HttpStatus.FORBIDDEN.value())
                .description(description)
                .build();
    }

    public static <T> CustomResponse<T> UNAUTHORIZED(){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED)
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .build();
    }

    public static <T> CustomResponse<T> INTERNAL_SERVER_ERROR(){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    public static <T> CustomResponse<T> INTERNAL_SERVER_ERROR(String description){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .description(description)
                .build();
    }

    public static <T> CustomResponse<T> JWT_EXPIRED(){
        return (CustomResponse<T>) CustomResponse.builder()
                .transaction_time(LocalDateTime.now())
                .description("JWT_EXPIRED")
                .statusCode(441)
                .build();
    }

}