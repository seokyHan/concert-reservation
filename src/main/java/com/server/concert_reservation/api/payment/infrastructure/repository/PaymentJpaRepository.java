package com.server.concert_reservation.api.payment.infrastructure.repository;

import com.server.concert_reservation.api.payment.infrastructure.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, Long> {
}
