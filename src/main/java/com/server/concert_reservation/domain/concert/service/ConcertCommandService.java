package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.dto.ReservationInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.model.ReservationOutbox;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.ReservationStatus;
import com.server.concert_reservation.interfaces.web.support.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class ConcertCommandService {

    private final ConcertReader concertReader;
    private final ConcertWriter concertWriter;
    private final TimeManager timeManager;

    public List<ConcertSeatInfo> reserveSeats(List<Long> seatIds) {
        val concertSeats = seatIds.stream()
                .map(concertReader::getConcertSeatById)
                .collect(Collectors.toList());
        concertSeats.forEach(ConcertSeat::temporaryReserve);

        return concertWriter.saveAll(concertSeats).stream()
                .map(ConcertSeatInfo::from)
                .collect(Collectors.toList());
    }

    public ReservationInfo createReservation(Long userId, Long concertScheduleId, List<ConcertSeatInfo> concertSeatInfos) {
        val totalPrice = concertSeatInfos.stream()
                .mapToInt(ConcertSeatInfo::price)
                .sum();

        val concertSeatIds = concertSeatInfos.stream()
                .map(ConcertSeatInfo::id)
                .collect(Collectors.toList());

        val reservation = Reservation.builder()
                .userId(userId)
                .concertScheduleId(concertScheduleId)
                .seatIds(concertSeatIds)
                .status(ReservationStatus.RESERVING)
                .totalPrice(totalPrice)
                .reservationAt(timeManager.now())
                .build();

        return ReservationInfo.from(concertWriter.saveReservation(reservation));
    }

    public void completeReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.complete();
        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::confirm);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }

    public void cancelTemporaryReservation(Long reservationId) {
        val reservation = concertReader.getReservationById(reservationId);
        reservation.cancelTemporaryReservation();

        val concertSeatList = concertReader.getConcertSeatsByIds(reservation.getSeatIds());
        concertSeatList.forEach(ConcertSeat::cancel);

        concertWriter.saveReservation(reservation);
        concertWriter.saveAll(concertSeatList);
    }

    public void createReservationOutbox(ReservationOutbox reservationOutbox) {
        concertWriter.saveReservationOutbox(reservationOutbox);
    }

    public void publishReservationOutbox(String key) {
        val reservationOutbox = concertReader.getReservationOutboxByKafkaMessageId(key);
        reservationOutbox.publish();
        concertWriter.saveReservationOutbox(reservationOutbox);
    }

}
