package com.server.concert_reservation.interfaces.web.support.uuid;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator implements UUIDManager {
    @Override
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}