package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.application.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static com.server.concert_reservation.api.concert.infrastructure.entity.types.SeatStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
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
    void onlyOneUserCanReserveTheSameSeatSimultaneously() {
        // given
        int threadCount = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        Set<Throwable> exceptions = new HashSet<>();

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    ReservationCommand command = new ReservationCommand((long) i + 1, 1L, List.of(1L, 2L), LocalDateTime.now());
                    concertCommandUseCase.temporaryReserveConcert(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failedCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(2, failedCount.get())
        );

        for (Throwable exception : exceptions) {
            assertAll(
                    () -> assertThat(exception).isInstanceOf(CustomException.class),
                    () -> assertEquals("예약 가능한 좌석이 아닙니다.", exception.getMessage())
            );
        }

    }

    @DisplayName("동시에 동일한 유저가 동일한 좌석에 예약 요청을 하는 경우 한번만 성공한다.")
    @Test
    void singleUserCanReserveSameSeatOnlyOnceConcurrently() {
        // given
        int threadCount = 10;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        Set<Throwable> exceptions = new HashSet<>();

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    ReservationCommand command = new ReservationCommand((long) i + 1, 1L, List.of(1L, 2L), LocalDateTime.now());
                    concertCommandUseCase.temporaryReserveConcert(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failedCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(9, failedCount.get())
        );

        for (Throwable exception : exceptions) {
            assertAll(
                    () -> assertThat(exception).isInstanceOf(CustomException.class),
                    () -> assertEquals("예약 가능한 좌석이 아닙니다.", exception.getMessage())
            );
        }
    }

    @DisplayName("동시에 여러 사용자가 동일한 좌석에 대해 예약 요청을 하는 경우 한명만 예약 성공한다. - 좌석 복수 선택")
    @Test
    void onlyOneUserCanReserveSameSeatsSimultaneously() {
        // given
        int threadCount = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        Set<Throwable> exceptions = new HashSet<>();

        // 좌석 매핑
        Map<Integer, List<Long>> userSeatMap = new HashMap<>();
        userSeatMap.put(1, List.of(1L, 2L));
        userSeatMap.put(2, List.of(2L, 3L));
        userSeatMap.put(3, List.of(1L, 3L));

        // when
        IntStream.range(0, threadCount).forEach(i -> {
            executorService.submit(() -> {
                try {
                    List<Long> seatIds = userSeatMap.get(i + 1);
                    ReservationCommand command = new ReservationCommand((long) i + 1, 1L, seatIds, LocalDateTime.now());
                    concertCommandUseCase.temporaryReserveConcert(command);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    failedCount.incrementAndGet();
                } finally {
                    countDownLatch.countDown();
                }
            });
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // then
        assertAll(
                () -> assertEquals(1, successCount.get()),
                () -> assertEquals(2, failedCount.get())
        );

        // 예외 검증
        for (Throwable exception : exceptions) {
            assertAll(
                    () -> assertThat(exception).isInstanceOf(CustomException.class),
                    () -> assertEquals("예약 가능한 좌석이 아닙니다.", exception.getMessage())
            );
        }
    }
}
