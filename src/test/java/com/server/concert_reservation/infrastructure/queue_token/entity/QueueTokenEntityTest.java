package com.server.concert_reservation.infrastructure.queue_token.entity;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueueTokenEntityTest {

    @DisplayName("Token 엔티티를 Token 도메인 모델로 변환한다.")
    @Test
    void tokenEntityCovertToTokenDomain() {
        //given
        QueueTokenEntity tokenEntity = Instancio.create(QueueTokenEntity.class);

        //when
        QueueToken token = tokenEntity.toDomain();

        //then
        assertEquals(tokenEntity.getId(), token.getId());
        assertEquals(tokenEntity.getUserId(), token.getUserId());
        assertEquals(tokenEntity.getToken(), token.getToken());
        assertEquals(tokenEntity.getStatus(), token.getStatus());

    }

}