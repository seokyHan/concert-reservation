package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api.token.application.dto.TokenCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.infrastructure.repository.TokenJpaRepository;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TokenCommandServiceIntegrationTest {

    @Autowired
    private TokenCommandUseCase tokenCommandUseCase;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    @BeforeEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        tokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("대기열 토큰을 생성한다.")
    @Test
    void createTokenTest() {
        // given
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

        TokenCommand tokenCommand = new TokenCommand(saveUser.getId(), "test-token");

        // when
        Token waitingToken1 = tokenCommandUseCase.createToken(tokenCommand);
        Token waitingToken2 = tokenCommandUseCase.createToken(tokenCommand);


        // then
        assertAll(
                () -> assertEquals(saveUser.getId(), waitingToken1.getUserId()),
                () -> assertEquals(WAITING, waitingToken1.getStatus()),
                () -> assertEquals(saveUser.getId(), waitingToken2.getUserId()),
                () -> assertEquals(WAITING, waitingToken2.getStatus())
        );
    }

}