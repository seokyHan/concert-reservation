package com.server.concert_reservation.domain.queue_token.model;

import com.server.concert_reservation.infrastructure.queue_token.entity.QueueTokenEntity;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.ALREADY_ACTIVATED;
import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.TOKEN_EXPIRED;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class QueueTokenTest {

    @Test
    @DisplayName("Token 활성화 테스트")
    void activateToken() {
        // given
        QueueToken token = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        LocalDateTime activatedAt = LocalDateTime.now();

        // when
        token.activate(activatedAt);

        // then
        assertEquals(activatedAt, token.getActivatedAt());
        assertEquals(ACTIVE, token.getStatus());
    }

    @Test
    @DisplayName("Token 만료 테스트")
    void expireToken() {
        // given
        QueueToken token = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();
        LocalDateTime expiredAt = LocalDateTime.now();

        // when
        token.expire(expiredAt);

        // then
        assertEquals(expiredAt, token.getExpiredAt());
        assertEquals(EXPIRED, token.getStatus());
    }

    @Test
    @DisplayName("Token 유효성 검사 테스트 - 이미 활성화된 경우")
    void validateTokenAlreadyActivated() {
        // given
        QueueToken token = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();

        // when & then
        assertThatThrownBy(() -> token.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_ACTIVATED.getMessage());
    }

    @Test
    @DisplayName("Token 유효성 검사 테스트 - 만료된 경우")
    void validateTokenWhenStatusExpired() {
        // given
        QueueToken token = Instancio.of(QueueToken.class)
                .set(field(QueueToken::getStatus), EXPIRED)
                .create();

        // when & then
        assertThatThrownBy(() -> token.validateToken())
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("TokenEntity 변환 테스트")
    void toEntityTest() {
        // given
        QueueToken token = Instancio.create(QueueToken.class);

        // when
        QueueTokenEntity tokenEntity = token.toEntity(token);

        // then
        assertNotNull(tokenEntity);
        assertEquals(token.getId(), tokenEntity.getId());
        assertEquals(token.getUserId(), tokenEntity.getUserId());
        assertEquals(token.getToken(), tokenEntity.getToken());
        assertEquals(token.getStatus(), tokenEntity.getStatus());
        assertEquals(token.getActivatedAt(), tokenEntity.getActivatedAt());
        assertEquals(token.getExpiredAt(), tokenEntity.getExpiredAt());
    }

}