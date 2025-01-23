package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.api.token.domain.repository.TokenWriter;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus.RESERVED;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus.RESERVING;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.SOLD;
import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.ACTIVE;
import static com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentConcurrentTest {

    @Autowired
    private PaymentUseCase paymentUseCase;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private UserReader userReader;
    @Autowired
    private ConcertWriter concertWriter;
    @Autowired
    private ConcertReader concertReader;
    @Autowired
    private TokenReader tokenReader;
    @Autowired
    private TokenWriter tokenWriter;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("동시에 예약된 콘서트를 5번 결제한다. - 낙관적 락")
    @Test
    void concurrentPaymentReservationTest() throws InterruptedException {
        // given
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

        Wallet wallet = Wallet.builder()
                .userId(saveUser.getId())
                .amount(100000)
                .build();
        userWriter.saveUserPoint(wallet.toEntity(wallet));

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .status(TEMPORARY_RESERVED)
                .build();
        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .status(TEMPORARY_RESERVED)
                .build();
        List<ConcertSeat> concertSeats = concertWriter.saveAll(List.of(concertSeat1, concertSeat2));

        Reservation reservation = Reservation.builder()
                .userId(saveUser.getId())
                .seatIds(List.of(concertSeats.get(0).getId(), concertSeats.get(1).getId()))
                .totalPrice(20000)
                .status(RESERVING)
                .build();
        Reservation saveReservationReservation = concertWriter.saveReservation(reservation);

        Token waitingToken = Token.builder()
                .token("test-token")
                .status(ACTIVE)
                .build();
        tokenWriter.save(waitingToken);

        PaymentCommand command = new PaymentCommand(saveUser.getId(), saveReservationReservation.getId(), "test-token");

        int threadCount = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        List<Long> durations = new ArrayList<>(); // 작업 소요 기록 List
        long startTime = System.nanoTime(); // 테스트 시작 시간
        Logger logger = LoggerFactory.getLogger(this.getClass());


        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                long taskStartTime = System.nanoTime();  // 작업 시작 시간
                try {
                    paymentUseCase.paymentReservation(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failedCount.incrementAndGet();
                    logger.error("결제 실패: {}", e.getMessage(), e);
                } finally {
                    long taskEndTime = System.nanoTime();  // 작업 종료 시간
                    durations.add(taskEndTime - taskStartTime);  // 작업 시간 계산 후 리스트에 추가
                    countDownLatch.countDown();
                }
            });
        });

        countDownLatch.await();
        long endTime = System.nanoTime();  // 전체 테스트 종료 시간

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(4, failedCount.get())
        );

        // 포인트 차감
        Wallet updatedUserPoint = userReader.getWalletByUserId(1L);
        assertThat(updatedUserPoint.getAmount()).isEqualTo(80000);

        // 결제된 예약건 상태 변경
        Reservation updatedConcertReservation = concertReader.getReservationById(1L);
        assertThat(updatedConcertReservation.getStatus()).isEqualTo(RESERVED);

        // 결제된 좌석 상태 변경
        List<ConcertSeat> updatedConcertSeats = concertReader.getConcertSeatsByIds(List.of(1L, 2L));
        assertAll(
                () -> assertThat(updatedConcertSeats.get(0).getStatus()).isEqualTo(SOLD),
                () -> assertThat(updatedConcertSeats.get(1).getStatus()).isEqualTo(SOLD)
        );

        // 대기열 만료
        Token updatedWaitingToken = tokenReader.getByToken("test-token");
        assertEquals(updatedWaitingToken.getStatus(), EXPIRED);

        long totalDuration = endTime - startTime;
        logger.info("전체 테스트 수행 시간 (ms): {}", totalDuration / 1_000_000);

        long minDuration = durations.stream().min(Long::compare).orElse(0L);
        logger.info("최소 소요 작업 시간 (ms): {}", minDuration / 1_000_000);

        long maxDuration = durations.stream().max(Long::compare).orElse(0L);
        logger.info("최대 소요 작업 시간 (ms): {}", maxDuration / 1_000_000);

        double avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
        logger.info("평균 소요 작업 시간 (ms): {}", avgDuration / 1_000_000);
    }
}
