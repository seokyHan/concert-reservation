package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.AVAILABLE;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;


@SpringBootTest
public class ReservationConcurrencyTest {

    @Autowired
    private ConcertCommandUseCase concertCommandUseCase;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private ConcertWriter concertWriter;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    @Order(1)
    void tearDown() {
        databaseCleanUp.execute();
    }

    @BeforeEach
    @Order(2)
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name("user1")
                .build();
        userWriter.save(user);

        Concert concert = Concert.builder()
                .title("콘서트1")
                .description("test")
                .build();
        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.minusDays(1))
                .reservationEndAt(now.plusDays(1))
                .build();
        concertWriter.save(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .price(10000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .price(20000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat3 = ConcertSeat.builder()
                .price(40000)
                .status(AVAILABLE)
                .build();
        concertWriter.saveAll(List.of(concertSeat1, concertSeat2, concertSeat3));
    }

    @DisplayName("동시에 여러 사용자가 동일한 좌석에 대해 예약 요청을 하는 경우 한명만 예약 성공한다.")
    @Test
    void onlyOneUserCanReserveTheSameSeatSimultaneously() throws InterruptedException {
        // given
        int threadCount = 5;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // when
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(() -> {
            try {
                ReservationCommand command = new ReservationCommand((long) i + 1, 1L, List.of(1L, 2L), LocalDateTime.now());
                concertCommandUseCase.temporaryReserveConcert(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failedCount.incrementAndGet();
            } finally {
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(4, failedCount.get())
        );

    }

    @DisplayName("동시에 동일한 유저가 동일한 좌석에 예약 요청을 하는 경우 한번만 성공한다.")
    @Test
    void singleUserCanReserveSameSeatOnlyOnceConcurrently() throws InterruptedException {
        // given
        Logger logger = LoggerFactory.getLogger(this.getClass());

        int threadCount = 300;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        List<Long> durations = new ArrayList<>(); // 작업 소요 기록 List
        long startTime = System.nanoTime(); // 테스트 시작 시간

        // when
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(() -> {
            long taskStartTime = System.nanoTime(); // 작업 시작 시간
            try {
                ReservationCommand command = new ReservationCommand((long) i + 1, 1L, List.of(1L, 2L), LocalDateTime.now());
                concertCommandUseCase.temporaryReserveConcert(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                logger.info("message : {}", e);
                failedCount.incrementAndGet();
            } finally {
                long taskEndTime = System.nanoTime();  // 작업 종료 시간
                durations.add(taskEndTime - taskStartTime); // 작업 시간 계산
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();
        long endTime = System.nanoTime();

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(299, failedCount.get())
        );

        long totalDuration = endTime - startTime;
        logger.info("전체 테스트 수행 시간 (ms): {}", totalDuration / 1_000_000);

        long minDuration = durations.stream().min(Long::compare).orElse(0L);
        logger.info("최소 소요 작업 시간 (ms): {}", minDuration / 1_000_000);

        long maxDuration = durations.stream().max(Long::compare).orElse(0L);
        logger.info("최대 소요 작업 시간 (ms): {}", maxDuration / 1_000_000);

        double avgDuration = durations.stream().mapToLong(Long::longValue).average().orElse(0.0);
        logger.info("평균 소요 작업 시간 (ms): {}", avgDuration / 1_000_000);
    }

    @DisplayName("동시에 여러 사용자가 동일한 좌석에 대해 예약 요청을 하는 경우 한명만 예약 성공한다. - 좌석 복수 선택")
    @Test
    void onlyOneUserCanReserveSameSeatsSimultaneously() throws InterruptedException {
        // given
        int threadCount = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        // 좌석 매핑
        Map<Integer, List<Long>> userSeatMap = new HashMap<>();
        userSeatMap.put(1, List.of(1L, 2L));
        userSeatMap.put(2, List.of(2L, 3L));
        userSeatMap.put(3, List.of(1L, 3L));

        // when
        IntStream.range(0, threadCount).forEach(i -> executorService.submit(() -> {
            try {
                List<Long> seatIds = userSeatMap.get(i + 1);
                ReservationCommand command = new ReservationCommand((long) i + 1, 1L, seatIds, LocalDateTime.now());
                concertCommandUseCase.temporaryReserveConcert(command);
                successCount.incrementAndGet();
            } catch (Exception e) {
                failedCount.incrementAndGet();
            } finally {
                countDownLatch.countDown();
            }
        }));
        countDownLatch.await();

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(2, failedCount.get())
        );
    }

}
