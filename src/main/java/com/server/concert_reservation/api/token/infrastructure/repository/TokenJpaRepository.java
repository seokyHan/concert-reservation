package com.server.concert_reservation.api.token.infrastructure.repository;

import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByUserIdAndToken(Long userId, String token);
    Optional<TokenEntity> findByToken(String token);


}
