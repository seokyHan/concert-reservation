package com.server.concert_reservation.api.concert.domain.service;

import com.server.concert_reservation.api.concert.application.ReservationUseCase;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.common.exception.CustomException;
import com.server.concert_reservation.common.time.TimeManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static com.server.concert_reservation.common.exception.code.ConcertErrorCode.CAN_NOT_RESERVE_DATE;

@RequiredArgsConstructor
@Service
public class ConcertCommandService implements ReservationUseCase {

    private final ConcertReader concertReader;
    private final ConcertWriter concertWriter;
    private final TimeManager timeManager;

    /**
     * 임시 예약
     */
    @Override
    public ReservationInfo temporaryReserveConcert(ReservationCommand command) {
        val concertSchedule = concertReader.getConcertScheduleById(command.concertScheduleId());
        if(concertSchedule.isAvailableReservePeriod(command.dateTime())){
            throw new CustomException(CAN_NOT_RESERVE_DATE);
        }

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
