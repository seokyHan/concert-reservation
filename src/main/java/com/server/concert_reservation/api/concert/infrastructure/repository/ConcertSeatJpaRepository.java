package com.server.concert_reservation.api.concert.infrastructure.repository;

import com.server.concert_reservation.api.concert.infrastructure.entity.ConcertSeatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertSeatJpaRepository extends JpaRepository<ConcertSeatEntity, Long> {
    List<ConcertSeatEntity> findByConcertScheduleId(Long concertScheduleId);

//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    Optional<ConcertSeatEntity> findWithPessimisticLockById(Long seatId);
}
