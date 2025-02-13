package com.server.concert_reservation.domain.send;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataPlatformSendService {

    public void send(Object event) {
        try {
            log.info("예약 데이터 전송 성공 : {}", event);
        } catch (Exception e) {
            log.error("예약 데이터 전송 실패: {}", e.getMessage());

        }
    }
}
