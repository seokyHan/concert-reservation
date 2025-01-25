package com.server.concert_reservation.api.token.infrastructure.entity;

import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import com.server.concert_reservation.support.api.common.uuid.UUIDManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TokenEntityTest {

    @Mock
    private UUIDManager uuidManager;

    @DisplayName("Token 엔티티를 Token 도메인 모델로 변환한다.")
    @Test
    void tokenEntityCovertToTokenDomainTest() {
        //given
        QueueTokenEntity tokenEntity = QueueTokenEntity.builder()
                .id(1L)
                .userId(1L)
                .token(uuidManager.generateUuid())
                .status(WAITING)
                .expiredAt(LocalDateTime.now().plusDays(1L))
                .build();

        //when
        QueueToken token = tokenEntity.toDomain();

        //then
        assertEquals(tokenEntity.getId(), token.getId());
        assertEquals(tokenEntity.getUserId(), token.getUserId());
        assertEquals(tokenEntity.getToken(), token.getToken());
        assertEquals(tokenEntity.getStatus(), token.getStatus());

    }

}