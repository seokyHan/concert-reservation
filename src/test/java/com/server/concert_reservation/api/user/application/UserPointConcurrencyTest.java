package com.server.concert_reservation.api.user.application;

import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.model.Wallet;
import com.server.concert_reservation.api.user.domain.repository.UserReader;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
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
    private UserCommandUseCase userCommandUseCase;

    @BeforeEach
    void tearDown() {
        databaseCleanUp.execute();
    }

    @DisplayName("동일한 사용자가 포인트를 동시에 5번 충전한다. - 비관적 락")
    @Test
    void concurrentPointRechargeTest() {
        //given
        Logger logger = LoggerFactory.getLogger(this.getClass());

        int threadCount = 5;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        Long userId = 1L;
        int point = 1000;
        userWriter.save(User.builder().name("홍길동").build());
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .amount(0)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));
        UserCommand userCommand = new UserCommand(userId, point);

        List<Long> durations = new ArrayList<>(); // 작업 소요 기록 List


        //when
        List<CompletableFuture<Void>> futures = IntStream.range(0, threadCount)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long taskStartTime = System.nanoTime(); // 작업 시작 시간
                    try {
                        userCommandUseCase.chargePoint(userCommand);
                        successCount.incrementAndGet(); // 성공 시 카운트 증가
                    } catch (Exception e) {
                        failedCount.incrementAndGet(); // 실패 시 카운트 증가
                    } finally {
                        long taskEndTime = System.nanoTime();  // 작업 종료 시간
                        durations.add(taskEndTime - taskStartTime); // 작업 시간 계산
                    }
                })).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        //then
        Wallet walletByUserId = userReader.getWalletByUserId(userId);
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
