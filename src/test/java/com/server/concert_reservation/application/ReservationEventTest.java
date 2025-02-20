package com.server.concert_reservation.application;

import com.server.concert_reservation.application.concert.ConcertUseCase;
import com.server.concert_reservation.application.concert.dto.ReservationCommand;
import com.server.concert_reservation.domain.concert.model.Concert;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.infrastructure.db.concert.entity.ReservationOutboxEntity;
import com.server.concert_reservation.infrastructure.db.concert.entity.types.OutboxStatus;
import com.server.concert_reservation.infrastructure.db.concert.repository.ReservationOutboxJpaRepository;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.RedisCacheManager;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.infrastructure.db.concert.entity.types.SeatStatus.AVAILABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class ReservationEventTest {

    @Autowired
    private ConcertUseCase concertUseCase;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private ConcertWriter concertWriter;
    @Autowired
    private ReservationOutboxJpaRepository reservationOutboxJpaRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private RedisCacheManager cacheManager;

    @BeforeEach
    @Order(1)
    void tearDown() {
        databaseCleanUp.execute();
    }

    @BeforeEach
    @Order(2)
    void clearCache() {
        cacheManager.getCache("availableConcertSchedule").clear();
        cacheManager.getCache("availableConcertSeats").clear();
        cacheManager.getCache("concertSchedule").clear();
    }

    @BeforeEach
    @Order(3)
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
        Concert savedConcert = concertWriter.saveConcert(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.minusDays(1))
                .build();
        concertWriter.saveConcertSchedule(concertSchedule);

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

    @DisplayName("좌석 예약에 성공한다. ")
    @Test
    void test() {
        //given
        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(1L, 2L), LocalDateTime.now());

        //when
        concertUseCase.reserveSeats(command);

        //then
        ReservationOutboxEntity reservationOutbox = reservationOutboxJpaRepository.findAll().get(0);

        assertAll(
                () -> assertThat(reservationOutbox.getKafkaMessageId()).isEqualTo("1"),
                () -> assertThat(reservationOutbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED)
        );
    }

}
