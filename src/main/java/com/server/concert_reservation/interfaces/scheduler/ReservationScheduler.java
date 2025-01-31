package com.server.concert_reservation.interfaces.scheduler;

import com.server.concert_reservation.application.concert.ConcertSchedulerUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ConcertSchedulerUseCase concertScheduleUseCase;

    @Scheduled(fixedDelayString = "60000")
    public void expireTemporaryReservations() {
        log.info("임시예약 만료 스케줄러 실행");
        val concertReservations = concertScheduleUseCase.getTemporaryReservationByExpired(5);
        concertReservations.forEach(concertReservation -> {
            try {
                concertScheduleUseCase.cancelReserveSeats(concertReservation.id());
            } catch (Exception e) {
                log.warn("임시예약 만료 중 오류 발생 (ID: {}): {}", concertReservation.id(), e.getMessage());
            }
        });
    }
}
