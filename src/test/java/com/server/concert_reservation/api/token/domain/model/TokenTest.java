package com.server.concert_reservation.api.token.domain.model;

import com.server.concert_reservation.api.token.infrastructure.entity.TokenEntity;
import com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus;
import com.server.concert_reservation.common.exception.CustomException;
import com.server.concert_reservation.common.uuid.UUIDManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static com.server.concert_reservation.common.exception.code.TokenErrorCode.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenTest {

    @Mock
    private UUIDManager uuidGenerator;

    @Test
    @DisplayName("Token 활성화 테스트")
    void activateTokenTest() {
        // given
        Token token = new Token(1L, uuidGenerator.generateUuid());
        LocalDateTime activatedAt = LocalDateTime.now();

        // when
        token.activate(activatedAt);

        // then
        assertTrue(token.isActivated());
        assertEquals(activatedAt, token.getActivatedAt());
    }

    @Test
    @DisplayName("Token 만료 테스트")
    void expireTokenTest() {
        // given
        Token token = new Token(1L, uuidGenerator.generateUuid());
        LocalDateTime expiredAt = LocalDateTime.now();

        // when
        token.expire(expiredAt);

        // then
        assertTrue(token.isExpired());
        assertEquals(expiredAt, token.getExpiredAt());
    }

    @Test
    @DisplayName("Token 유효성 검사 테스트 - 이미 활성화된 경우")
    void validateTokenAlreadyActivatedTest() {
        // given
        Token token = new Token(1L, uuidGenerator.generateUuid());
        token.activate(LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> token.activate(LocalDateTime.now()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ALREADY_ACTIVATED.getMessage());
    }

    @Test
    @DisplayName("Token 유효성 검사 테스트 - 만료된 경우")
    void validateTokenExpiredTest() {
        // given
        Token token = new Token(1L, uuidGenerator.generateUuid());
        token.expire(LocalDateTime.now());

        // when & then
        assertThatThrownBy(() -> token.validateToken())
                .isInstanceOf(CustomException.class)
                .hasMessage(TOKEN_EXPIRED.getMessage());
    }

    @Test
    @DisplayName("TokenEntity 변환 테스트")
    void toEntityTest() {
        // given
        Token token = createToken(1L, 1L, uuidGenerator.generateUuid(), TokenStatus.WAITING, LocalDateTime.now(), LocalDateTime.now().plusDays(1), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());

        // when
        TokenEntity tokenEntity = token.toEntity(token);

        // then
        assertNotNull(tokenEntity);
        assertEquals(token.getId(), tokenEntity.getId());
        assertEquals(token.getUserId(), tokenEntity.getUserId());
        assertEquals(token.getToken(), tokenEntity.getToken());
        assertEquals(token.getStatus(), tokenEntity.getStatus());
        assertEquals(token.getActivatedAt(), tokenEntity.getActivatedAt());
        assertEquals(token.getExpiredAt(), tokenEntity.getExpiredAt());
        assertEquals(token.getLastActionedAt(), tokenEntity.getLastActionedAt());
    }

    private Token createToken(Long id, Long userId, String token, TokenStatus status, LocalDateTime activatedAt, LocalDateTime expiredAt, LocalDateTime lastActionedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return Token.of(id, userId, token, status, activatedAt, expiredAt, lastActionedAt, createdAt, updatedAt);
    }


}