package com.server.concert_reservation.domain.queue_token.service;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus;
import com.server.concert_reservation.support.DatabaseCleanUp;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.*;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class QueueTokenCommandServiceIntegrationTest {

    @Autowired
    private QueueTokenCommandService queueTokenCommandService;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private QueueTokenWriter queueTokenWriter;
    @Autowired
    private QueueTokenReader queueTokenReader;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createTokenTest() {
        // given
        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        // when
        QueueTokenInfo waitingToken1 = queueTokenCommandService.createToken(savedUser.getId());
        QueueTokenInfo waitingToken2 = queueTokenCommandService.createToken(savedUser.getId());

        // then
        assertAll(
                () -> assertEquals(savedUser.getId(), waitingToken1.userId()),
                () -> assertEquals(WAITING, waitingToken1.status()),
                () -> assertEquals(savedUser.getId(), waitingToken2.userId()),
                () -> assertEquals(WAITING, waitingToken2.status())
        );
    }

    @DisplayName("토큰 상태가 Expired 또는 Waiting 상태라면 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideCheckActivatedTokenStatuses")
    void shouldThrownExceptionWhenStatusExpiredOrWaiting(QueueTokenStatus status, String expectedMessage) {
        //given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getStatus), status)
                .create();
        queueTokenWriter.save(queueToken);

        //when & then
        assertThatThrownBy(() -> queueTokenCommandService.checkActivatedToken(queueToken.getToken()))
                .isInstanceOf(CustomException.class)
                .hasMessage(expectedMessage);
    }

    @DisplayName("토큰 상태가 Active 또는 Expired 상태에서 토큰 활성화를 요청하면 예외가 발생한다.")
    @ParameterizedTest
    @MethodSource("provideActivateTokenStatuses")
    void shouldThrownExceptionWhenStatusExpiredOrActiveRequest(QueueTokenStatus status, String expectedMessage) {
        //given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getStatus), status)
                .create();
        queueTokenWriter.save(queueToken);

        //when & then
        assertThatThrownBy(() -> queueTokenCommandService.activateToken(queueToken.getToken()))
                .isInstanceOf(CustomException.class)
                .hasMessage(expectedMessage);
    }

    @DisplayName("토큰 상태를 ACTIVE 상태로 활성화 한다.")
    @Test
    void activateTokenStatusToActive() {
        //given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getStatus), WAITING)
                .create();
        QueueToken savedQueueToken = queueTokenWriter.save(queueToken);

        queueTokenCommandService.activateToken(savedQueueToken.getToken());
        QueueToken getQueueToken = queueTokenReader.getByToken(savedQueueToken.getToken());

        //when & then
        assertEquals(ACTIVE, getQueueToken.getStatus());
    }

    @DisplayName("토큰 상태를 EXPIRED 상태로 만 한다.")
    @Test
    void expireTokenStatusToExpired() {
        //given
        QueueToken queueToken = Instancio.of(QueueToken.class)
                .ignore(field(QueueToken::getId))
                .set(field(QueueToken::getStatus), ACTIVE)
                .create();
        QueueToken savedQueueToken = queueTokenWriter.save(queueToken);

        queueTokenCommandService.expireToken(savedQueueToken.getToken());
        QueueToken getQueueToken = queueTokenReader.getByToken(savedQueueToken.getToken());

        //when & then
        assertEquals(EXPIRED, getQueueToken.getStatus());
    }

    private static Stream<Arguments> provideCheckActivatedTokenStatuses() {
        return Stream.of(
                Arguments.of(EXPIRED, TOKEN_EXPIRED.getMessage()),
                Arguments.of(WAITING, TOKEN_NOT_ACTIVATED.getMessage())
        );
    }

    private static Stream<Arguments> provideActivateTokenStatuses() {
        return Stream.of(
                Arguments.of(EXPIRED, TOKEN_EXPIRED.getMessage()),
                Arguments.of(ACTIVE, ALREADY_ACTIVATED.getMessage())
        );
    }


}