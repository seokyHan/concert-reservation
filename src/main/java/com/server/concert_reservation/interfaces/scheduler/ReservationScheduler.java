package com.server.concert_reservation.interfaces.scheduler;

import com.server.concert_reservation.api_backup.concert.application.ConcertCommandUseCase;
import com.server.concert_reservation.api_backup.concert.application.ConcertQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ConcertCommandUseCase reservationUseCase;
    private final ConcertQueryUseCase getConcertUseCase;

    @Scheduled(fixedDelayString = "60000")
    public void expireTemporaryReservations() {
        log.info("임시예약 만료 스케줄러 실행");
        val concertReservations = getConcertUseCase.getTemporaryReservationByExpired(5);
        concertReservations.forEach(concertReservation -> {
            try {
                reservationUseCase.cancelTemporaryReservation(concertReservation.getId());
            } catch (Exception e) {
                log.warn("임시예약 만료 중 오류 발생 (ID: {}): {}", concertReservation.getId(), e.getMessage());
            }
        });
    }
}
