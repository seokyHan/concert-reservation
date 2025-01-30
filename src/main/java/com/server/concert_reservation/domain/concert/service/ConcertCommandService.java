package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConcertCommandService {

    private final ConcertReader concertReader;
    private final ConcertWriter concertWriter;
    private final TimeManager timeManager;

    /**
     * 좌석 임시 예약
     */
    public ReservationInfo reserveSeats(Long userId, List<Long> seatIds) {
        val concertSeats = seatIds.stream()
                .map(concertReader::getConcertSeatById)
                .collect(Collectors.toList());
        concertSeats.forEach(ConcertSeat::temporaryReserve);
        concertWriter.saveAll(concertSeats);

        val totalPrice = concertSeats.stream()
                .mapToInt(ConcertSeat::getPrice)
                .sum();

        return concertWriter.saveReservation(
                Reservation.createReservation(userId, seatIds, totalPrice, timeManager.now())
        );

    }

    /**
     * 예약 확정(결제 완료된 시점)
     */
    public void completeReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.complete();
        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::confirm);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }

    /**
     * 좌석 임시 예약 취소(스케쥴러)
     */
    public void cancelTemporaryReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.cancelTemporaryReservation();

        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::cancel);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }

    public void checkConcertSchedule(Long concertScheduleId, LocalDateTime dateTime) {
        val concertSchedule = concertReader.getConcertScheduleById(concertScheduleId);
        concertSchedule.isAvailableReservePeriod(dateTime);
    }
}
