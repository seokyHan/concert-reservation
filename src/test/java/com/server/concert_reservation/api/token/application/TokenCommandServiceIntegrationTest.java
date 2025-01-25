package com.server.concert_reservation.api.token.application;

import com.server.concert_reservation.api_backup.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api_backup.token.application.dto.TokenCommand;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.WAITING;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TokenCommandServiceIntegrationTest {

    @Autowired
    private TokenCommandUseCase tokenCommandUseCase;
    @Autowired
    private UserWriter userWriter;
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
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

        TokenCommand tokenCommand = new TokenCommand(saveUser.getId(), "test-token");

        // when
        QueueToken waitingToken1 = tokenCommandUseCase.createToken(tokenCommand);
        QueueToken waitingToken2 = tokenCommandUseCase.createToken(tokenCommand);


        // then
        assertAll(
                () -> assertEquals(saveUser.getId(), waitingToken1.getUserId()),
                () -> assertEquals(WAITING, waitingToken1.getStatus()),
                () -> assertEquals(saveUser.getId(), waitingToken2.getUserId()),
                () -> assertEquals(WAITING, waitingToken2.getStatus())
        );
    }

}