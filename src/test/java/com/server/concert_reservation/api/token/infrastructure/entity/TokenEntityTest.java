package com.server.concert_reservation.api.token.infrastructure.entity;

import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.common.uuid.UUIDManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.WAITING;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenEntityTest {

    @Mock
    private UUIDManager uuidManager;

    @DisplayName("Token 엔티티를 Token 도메인 모델로 변환한다.")
    @Test
    void tokenEntityCovertToTokenDomainTest() {
        //given
        TokenEntity tokenEntity = TokenEntity.builder()
                .id(1L)
                .userId(1L)
                .token(uuidManager.generateUuid())
                .status(WAITING)
                .expiredAt(LocalDateTime.now().plusDays(1L))
                .build();

        //when
        Token token = tokenEntity.toDomain();

        //then
        assertEquals(tokenEntity.getId(), token.getId());
        assertEquals(tokenEntity.getUserId(), token.getUserId());
        assertEquals(tokenEntity.getToken(), token.getToken());
        assertEquals(tokenEntity.getStatus(), token.getStatus());

    }

}