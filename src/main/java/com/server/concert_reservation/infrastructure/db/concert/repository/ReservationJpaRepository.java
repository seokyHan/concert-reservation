package com.server.concert_reservation.infrastructure.db.concert.repository;

import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<ReservationEntity, Long> {

    @Query("""
                SELECT re
                FROM ReservationEntity re
                WHERE re.status = 'RESERVING'
                AND re.reservationAt <= :expirationTime
            """)
    List<ReservationEntity> findTemporaryReservationsToBeExpired(@Param("expirationTime") LocalDateTime expirationTime);
}
