package com.server.concert_reservation.api.concert.infrastructure.repository;

import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertSeatEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeatEntity, Long> {
    List<ConcertSeatEntity> findByConcertScheduleId(Long concertScheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ConcertSeatEntity> findWithPessimisticLockById(Long seatId);
}
