package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api_backup.payment.application.PaymentUseCase;
import com.server.concert_reservation.api_backup.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api_backup.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.model.Reservation;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import com.server.concert_reservation.domain.concert.repository.ConcertWriter;
import com.server.concert_reservation.domain.queue_token.model.QueueToken;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenReader;
import com.server.concert_reservation.domain.queue_token.repository.QueueTokenWriter;
import com.server.concert_reservation.domain.user.model.User;
import com.server.concert_reservation.domain.user.model.Wallet;
import com.server.concert_reservation.domain.user.repository.UserReader;
import com.server.concert_reservation.domain.user.repository.UserWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.SOLD;
import static com.server.concert_reservation.infrastructure.concert.entity.types.SeatStatus.TEMPORARY_RESERVED;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.EXPIRED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentServiceIntegrationTest {

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
    private QueueTokenReader tokenReader;
    @Autowired
    private QueueTokenWriter tokenWriter;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("임시 예약된 콘서트를 결제한다.")
    @Test
    void paymentReservationTest() {
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


        QueueToken waitingToken = QueueToken.builder()
                .token("test-token")
                .status(ACTIVE)
                .build();
        tokenWriter.save(waitingToken);

        PaymentCommand command = new PaymentCommand(saveUser.getId(), saveReservationReservation.getId(), "test-token");


        // when
        PaymentInfo paymentInfo = paymentUseCase.paymentReservation(command);

        // then
        assertAll(
                () -> assertThat(paymentInfo.id()).isNotNull(),
                () -> assertThat(paymentInfo.userId()).isEqualTo(saveUser.getId()),
                () -> assertThat(paymentInfo.reservationId()).isEqualTo(saveReservationReservation.getId()),
                () -> assertThat(paymentInfo.amount()).isEqualTo(20000)
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
        QueueToken updatedWaitingToken = tokenReader.getByToken("test-token");
        assertEquals(updatedWaitingToken.getStatus(), EXPIRED);
    }

}