package com.server.concert_reservation.api.concert.scheduler;

import com.server.concert_reservation.api.concert.application.GetConcertUseCase;
import com.server.concert_reservation.api.concert.application.ReservationUseCase;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationUseCase reservationUseCase;
    private final GetConcertUseCase getConcertUseCase;

    @Scheduled(fixedDelayString = "60000")
    public void expireTemporaryReservations() {
        log.info("임시예약 만료 스케줄러 실행");
        final List<Reservation> concertReservations = getConcertUseCase.getTemporaryReservationByExpired(5);
        concertReservations.forEach(concertReservation -> {
            try {
                reservationUseCase.cancelTemporaryReservation(concertReservation.getId());
            } catch (Exception e) {
                log.warn("임시예약 만료 중 오류 발생 (ID: {}): {}", concertReservation.getId(), e.getMessage());
            }
        });
    }
}
