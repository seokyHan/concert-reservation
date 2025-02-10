package com.server.concert_reservation.interfaces.web.support.exception;

import org.springframework.boot.logging.LogLevel;
import org.springframework.http.HttpStatus;

public interface ErrorType {
    String getCode();

    HttpStatus getStatus();

    String getMessage();

    LogLevel getLogLevel();
}