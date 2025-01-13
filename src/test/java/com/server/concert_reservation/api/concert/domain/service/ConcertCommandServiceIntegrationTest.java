package com.server.concert_reservation.api.concert.domain.service;

import com.server.concert_reservation.api.concert.application.ReservationUseCase;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationInfo;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertScheduleJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ConcertSeatJpaRepository;
import com.server.concert_reservation.api.concert.infrastructure.repository.ReservationJpaRepository;
import com.server.concert_reservation.api.user.domain.model.User;
import com.server.concert_reservation.api.user.domain.repository.UserWriter;
import com.server.concert_reservation.api.user.infrastructure.repository.UserJpaRepository;
import com.server.concert_reservation.common.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.concert_reservation.api.concert.infrastructure.types.ReservationStatus.RESERVING;
import static com.server.concert_reservation.api.concert.infrastructure.types.SeatStatus.AVAILABLE;
import static com.server.concert_reservation.api.concert.infrastructure.types.SeatStatus.TEMPORARY_RESERVED;
import static com.server.concert_reservation.common.exception.code.ConcertErrorCode.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertCommandServiceIntegrationTest {

    @Autowired
    private ReservationUseCase reservationUseCase;
    @Autowired
    private UserWriter userWriter;
    @Autowired
    private ConcertWriter concertWriter;
    @Autowired
    private ConcertReader concertReader;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ConcertScheduleJpaRepository concertScheduleJpaRepository;
    @Autowired
    private ConcertSeatJpaRepository concertSeatJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;


    @BeforeEach
    void tearDown() {
        userJpaRepository.deleteAllInBatch();
        concertJpaRepository.deleteAllInBatch();
        concertScheduleJpaRepository.deleteAllInBatch();
        concertSeatJpaRepository.deleteAllInBatch();
        reservationJpaRepository.deleteAllInBatch();

    }

    @DisplayName("콘서트 좌석 임시 예약 성공 테스트")
    @Test
    void temporaryReserveConcertTest() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

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
        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .price(10000)
                .status(AVAILABLE)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .price(20000)
                .status(AVAILABLE)
                .build();
        List<ConcertSeat> savedConcertSeats = concertWriter.saveAll(List.of(concertSeat1, concertSeat2));

        // when
        ReservationCommand command = new ReservationCommand(saveUser.getId(),
                saveConcertSchedule.getId(),
                List.of(savedConcertSeats.get(0).getId(), savedConcertSeats.get(1).getId()),
                now
        );
        ReservationInfo concertReservationInfo = reservationUseCase.temporaryReserveConcert(command);

        // then
        assertAll(
                () -> assertThat(concertReservationInfo.id()).isNotNull(),
                () -> assertThat(concertReservationInfo.userId()).isEqualTo(saveUser.getId()),
                () -> assertThat(concertReservationInfo.seatIds()).hasSize(2),
                () -> assertThat(concertReservationInfo.status()).isEqualTo(RESERVING),
                () -> assertThat(concertReservationInfo.totalPrice()).isEqualTo(30000)
        );

        List<ConcertSeat> concertSeats = concertReader.getConcertSeatsByIds(List.of(savedConcertSeats.get(0).getId(), savedConcertSeats.get(1).getId()));
        assertAll(
                () -> assertThat(concertSeats.get(0).getStatus()).isEqualTo(TEMPORARY_RESERVED),
                () -> assertThat(concertSeats.get(1).getStatus()).isEqualTo(TEMPORARY_RESERVED)
        );
    }

    @DisplayName("콘서트 예약 가능한 기간에 예약을 하려는 경우 예외가 발생한다.")
    @Test
    void notAvailablePeriodThrowException() {

        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

        Concert concert = Concert.builder()
                .title("콘서트1")
                .description("test")
                .build();
        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.plusDays(2L))
                .reservationEndAt(now.plusDays(1L))
                .build();
        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        ReservationCommand command = new ReservationCommand(saveUser.getId(),
                saveConcertSchedule.getId(),
                List.of(1L, 2L),
                now);

        // when & then
        assertThatThrownBy(() -> reservationUseCase.temporaryReserveConcert(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(CONCERT_SEAT_NOT_FOUND.getMessage());

    }

    @DisplayName("이미 예약된 좌석인 경우 예외가 발생한다")
    @Test
    void alreadyReservationSeatThrowException() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .name("user1")
                .build();
        User saveUser = userWriter.save(user);

        Concert concert = Concert.builder()
                .title("콘서트1")
                .description("test")
                .build();
        Concert savedConcert = concertWriter.save(concert);

        ConcertSchedule concertSchedule = ConcertSchedule.builder()
                .concertId(savedConcert.getId())
                .remainTicket(10)
                .reservationStartAt(now.minusDays(1L))
                .reservationEndAt(now.plusDays(1L))
                .build();
        ConcertSchedule saveConcertSchedule = concertWriter.save(concertSchedule);

        ConcertSeat concertSeat1 = ConcertSeat.builder()
                .price(10000)
                .status(TEMPORARY_RESERVED)
                .build();

        ConcertSeat concertSeat2 = ConcertSeat.builder()
                .price(20000)
                .status(AVAILABLE)
                .build();
        List<ConcertSeat> savedConcertSeats = concertWriter.saveAll(List.of(concertSeat1, concertSeat2));

        ReservationCommand command = new ReservationCommand(saveUser.getId(),
                saveConcertSchedule.getId(),
                List.of(savedConcertSeats.get(0).getId(), savedConcertSeats.get(1).getId()),
                now
        );

        // when & then
        assertThatThrownBy(() -> reservationUseCase.temporaryReserveConcert(command))
                .isInstanceOf(CustomException.class)
                .hasMessage(CAN_NOT_RESERVE_SEAT.getMessage());
    }

}