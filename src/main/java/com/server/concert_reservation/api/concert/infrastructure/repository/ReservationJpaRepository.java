package com.server.concert_reservation.api.concert.infrastructure.repository;

import com.server.concert_reservation.api.concert.infrastructure.entity.ReservationEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("""
                SELECT re
                FROM ReservationEntity re
                WHERE re.status = 'RESERVING'
                AND re.reservationAt <= :expirationTime
            """)
    List<ReservationEntity> findTemporaryReservationsToBeExpired(@Param("expirationTime") LocalDateTime expirationTime);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReservationEntity> findWithLockById(Long reservationId);
}
