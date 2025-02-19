package com.server.concert_reservation.infrastructure.db.payment.repository;

import com.server.concert_reservation.infrastructure.db.payment.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
}
