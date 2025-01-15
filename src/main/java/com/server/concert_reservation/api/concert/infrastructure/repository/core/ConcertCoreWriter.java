package com.server.concert_reservation.api.concert.infrastructure.repository.core;


import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertScheduleEntity;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertSeatEntity;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertScheduleJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertSeatJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ReservationJpaRepository;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ConcertCoreWriter implements ConcertWriter {
    private  final ConcertJpaRepository concertJpaRepository;
    private final ConcertSeatJpaRepository concertSeatJpaRepository;
    private final ConcertScheduleJpaRepository concertScheduleJpaRepository;
    private final ReservationJpaRepository reservationJpaRepository;
    private final TimeManager timeManager;

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
        return reservationJpaRepository.save(reservation.toEntity(reservation, timeManager.now())).toDomain();
    }

    @Override
    public ConcertSchedule save(ConcertSchedule concertSchedule) {
        return concertScheduleJpaRepository.save(new ConcertScheduleEntity(concertSchedule)).toDomain();
    }

    @Override
    public Concert save(Concert concert) {
        return concertJpaRepository.save(new ConcertEntity(concert)).toDomain();
    }
}
