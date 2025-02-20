package com.server.concert_reservation.infrastructure.db.concert.repository.core;


import com.server.concert_reservation.domain.concert.model.*;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertScheduleEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.ConcertSeatEntity;
import com.server.concert_reservation.infrastructure.db.concert.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ConcertCoreWriter implements ConcertWriter {
    private final ConcertJpaRepository concertJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ReservationOutboxJpaRepository reservationOutboxJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;

    @Override
    public List<ConcertSeat> saveAll(List<ConcertSeat> concertSeats) {
        val concertSeatEntities = concertSeats.stream()
                .map(concertSeat -> concertSeat.toEntity(concertSeat))
                .collect(Collectors.toList());

        return concertSeatJpaRepository.saveAll(concertSeatEntities).stream()
                .map(ConcertSeatEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Reservation saveReservation(Reservation reservation) {
        return reservationJpaRepository.save(reservation.toEntity(reservation)).toDomain();
    }

    @Override
    public ConcertSchedule saveConcertSchedule(ConcertSchedule concertSchedule) {
        return concertScheduleJpaRepository.save(new ConcertScheduleEntity(concertSchedule)).toDomain();
    }

    @Override
    public Concert saveConcert(Concert concert) {
        return concertJpaRepository.save(new ConcertEntity(concert)).toDomain();
    }

    @Override
    public ReservationOutbox saveReservationOutbox(ReservationOutbox reservationOutbox) {
        return reservationOutboxJpaRepository.save(reservationOutbox.toEntity(reservationOutbox)).toDomain();
    }
}
