package com.server.concert_reservation.support.api.common.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeProvider implements TimeManager {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}