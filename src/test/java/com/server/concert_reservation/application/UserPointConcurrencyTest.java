package com.server.concert_reservation.application;

import com.server.concert_reservation.application.user.UserUseCase;
import com.server.concert_reservation.application.user.dto.UserCommand;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class UserPointConcurrencyTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserReader userReader;
    @Autowired
    private UserUseCase userUseCase;

    @BeforeEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @DisplayName("동일한 사용자가 포인트를 동시에 5번 충전한다. - 비관적 락")
    @Test
    void concurrentPointRechargeTest() {
        //given
        Logger logger = LoggerFactory.getLogger(this.getClass());
        List<Long> durations = new ArrayList<>(); // 작업 소요 기록 List

        int threadCount = 5;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        User user = Instancio.of(User.class)
                .ignore(field(User::getId))
                .create();
        User savedUser = userWriter.save(user);

        Wallet wallet = Instancio.of(Wallet.class)
                .ignore(field(Wallet::getId))
                .set(field(Wallet::getUserId), savedUser.getId())
                .set(field(Wallet::getAmount), 0)
                .create();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand command = UserCommand.of(savedUser.getId(), 1000);

        //when
        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long taskStartTime = System.nanoTime(); // 작업 시작 시간
                    try {
                        userUseCase.chargePoint(command);
                        successCount.incrementAndGet(); // 성공 시 카운트 증가
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("message : " + e.getMessage());
                        failedCount.incrementAndGet(); // 실패 시 카운트 증가
                    } finally {
                        long taskEndTime = System.nanoTime();  // 작업 종료 시간
                        durations.add(taskEndTime - taskStartTime); // 작업 시간 계산
                    }
                })).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        //then
        Wallet walletByUserId = userReader.getWalletByUserId(savedUser.getId());
        assertAll(
                () -> assertThat(walletByUserId.getAmount()).isEqualTo(5000),
                () -> assertThat(successCount.get()).isEqualTo(5),
                () -> assertThat(failedCount.get()).isEqualTo(0)
        );

        long minDuration = durations.stream().min(Long::compare).orElse(0L);
        logger.info("최소 소요 작업 시간 (ms): {}", minDuration / 1_000_000);

        long maxDuration = durations.stream().max(Long::compare).orElse(0L);
        logger.info("최대 소요 작업 시간 (ms): {}", maxDuration / 1_000_000);

        double avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
        logger.info("평균 소요 작업 시간 (ms): {}", avgDuration / 1_000_000);
    }
}
