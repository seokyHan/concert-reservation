package com.server.concert_reservation.api.payment.domain.service;

import com.server.concert_reservation.api.concert.application.GetConcertUseCase;
import com.server.concert_reservation.api.concert.application.ReservationUseCase;
import com.server.concert_reservation.api.payment.application.PaymentUseCase;
import com.server.concert_reservation.api.payment.domain.model.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.domain.model.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.domain.model.Payment;
import com.server.concert_reservation.api.payment.domain.repository.PaymentWriter;
import com.server.concert_reservation.api.token.application.TokenUseCase;
import com.server.concert_reservation.api.user.application.PointUseCase;
import com.server.concert_reservation.api.user.domain.model.dto.UserCommand;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService implements PaymentUseCase {

    private final PaymentWriter paymentWriter;
    private final PointUseCase pointUseCase;
    private final GetConcertUseCase getConcertUseCase;
    private final ReservationUseCase reservationUseCase;
    private final TokenUseCase tokenUseCase;

    @Override
    public PaymentInfo paymentReservation(PaymentCommand command) {
        val reservation = getConcertUseCase.getReservation(command.reservationId());
        reservation.isTemporaryReserved();

        pointUseCase.usePoint(UserCommand.from(command.userId(), reservation.getTotalPrice()));
        val payment = paymentWriter.save(Payment.create(command, reservation.getTotalPrice()));

        reservationUseCase.completeReservation(command.reservationId());
        tokenUseCase.expireToken(command.token());

        return PaymentInfo.of(payment);
    }
}
