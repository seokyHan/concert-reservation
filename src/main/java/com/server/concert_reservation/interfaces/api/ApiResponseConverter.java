package com.server.concert_reservation.interfaces.api;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponseConverter<T> {

    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    public ApiResponseConverter(HttpStatus status, String message, T data) {
        this.code = status.value();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseConverter<T> of(HttpStatus httpStatus, String message, T data) {
        return new ApiResponseConverter<>(httpStatus, message, data);
    }

    public static <T> ApiResponseConverter<T> of(HttpStatus httpStatus, T data) {
        return of(httpStatus, httpStatus.name(), data);
    }

    public static <T> ApiResponseConverter<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }

    public static <T> ApiResponseConverter<T> created(T data) {
        return of(HttpStatus.CREATED, data);
    }
}
