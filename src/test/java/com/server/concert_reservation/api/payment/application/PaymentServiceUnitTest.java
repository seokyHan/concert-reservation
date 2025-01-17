package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.concert.application.ConcertQueryUseCase;
import com.server.concert_reservation.api.concert.application.ConcertCommandUseCase;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.infrastructure.entity.types.ReservationStatus;
import com.server.concert_reservation.api.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.domain.model.Payment;
import com.server.concert_reservation.api.payment.domain.repository.PaymentWriter;
import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.user.application.UserQueryUseCase;
import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.server.concert_reservation.api.concert.domain.errorcode.ConcertErrorCode.IS_NOT_TEMPORARY_RESERVATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceUnitTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentWriter paymentWriter;
    @Mock
    private ConcertQueryUseCase getConcertUseCase;
    @Mock
    private TokenCommandUseCase tokenUseCase;
    @Mock
    private ConcertCommandUseCase reservationUseCase;
    @Mock
    private UserQueryUseCase pointUseCase;

    @DisplayName("임시 예약중이 아닌경우 얘외가 발생한다.")
    @Test
    void isNotReservingThrowException() {
        // given
        Long reservationId = 1L;
        PaymentCommand paymentCommand = new PaymentCommand(1L, reservationId, "test-token");
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.CANCELED)
                .seatIds(List.of(1L, 2L))
                .build();
        when(getConcertUseCase.getReservation(reservationId)).thenReturn(reservation);

        // when & then
        assertThatThrownBy(() -> paymentService.paymentReservation(paymentCommand))
                .isInstanceOf(CustomException.class)
                .hasMessage(IS_NOT_TEMPORARY_RESERVATION.getMessage());
    }

    @DisplayName("결제 테스트")
    @Test
    void paymentReservationTest() {
        // given
        Long reservationId = 1L;
        Long userId = 1L;
        String token = "test-token";
        int totalPrice = 10000;

        PaymentCommand command = new PaymentCommand(userId, reservationId, token);
        Reservation reservation = mock(Reservation.class);
        Payment payment = mock(Payment.class);

        when(getConcertUseCase.getReservation(reservationId)).thenReturn(reservation);
        when(reservation.getTotalPrice()).thenReturn(totalPrice);
        when(paymentWriter.save(any(Payment.class))).thenReturn(payment);

        // When
        PaymentInfo paymentInfo = paymentService.paymentReservation(command);

        // Then
        verify(reservation).isTemporaryReserved();
        verify(pointUseCase).usePoint(UserCommand.from(userId, totalPrice));
        verify(paymentWriter).save(any(Payment.class));
        verify(reservationUseCase).completeReservation(reservationId);
        verify(tokenUseCase).expireToken(token);
        assertNotNull(paymentInfo);
    }

}