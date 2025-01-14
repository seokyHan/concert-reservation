package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.support.time.TimeManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ConcertCommandService implements ConcertCommandUseCase {

    private final ConcertReader concertReader;
    private final ConcertWriter concertWriter;
    private final TimeManager timeManager;

    /**
     * 임시 예약
     */
    @Override
    public ReservationInfo temporaryReserveConcert(ReservationCommand command) {
        val concertSchedule = concertReader.getConcertScheduleById(command.concertScheduleId());
        concertSchedule.isAvailableReservePeriod(command.dateTime());

        val concertSeatList = command.seatIds().stream()
                .map(concertReader::getConcertSeatById)
                .collect(Collectors.toList());
        concertSeatList.forEach(ConcertSeat::temporaryReserve);
        val savedConcertSeatList = concertWriter.saveAll(concertSeatList);
        val totalPrice = concertSeatList.stream()
                .mapToInt(ConcertSeat::getPrice)
                .sum();

        val reservation = Reservation.createReservation(command, totalPrice, timeManager.now());
        val savedReservation = concertWriter.saveReservation(reservation);

        return ReservationInfo.of(savedReservation, savedConcertSeatList);
    }

    /**
     * 예약 확정(결제 완료된 시점 - 결제 통합테스트)
     */
    @Override
    public void completeReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.complete();
        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::confirm);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }

    @Transactional
    public void cancelTemporaryReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.cancelTemporaryReservation();

        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::cancel);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }
}
