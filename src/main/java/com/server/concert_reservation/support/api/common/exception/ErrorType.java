package com.server.concert_reservation.support.api.common.exception;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public interface ErrorType {
    String getCode();
    HttpStatus getStatus();
    String getMessage();
    LogLevel getLogLevel();
}