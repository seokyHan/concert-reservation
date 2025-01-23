package com.server.concert_reservation.api.payment.application;

import com.server.concert_reservation.api.concert.application.ConcertCommandUseCase;
import com.server.concert_reservation.api.concert.application.ConcertQueryUseCase;
import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.domain.model.Payment;
import com.server.concert_reservation.api.payment.domain.repository.PaymentWriter;
import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.user.application.UserCommandUseCase;
import com.server.concert_reservation.api.user.application.dto.UserCommand;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService implements PaymentUseCase {

    private final PaymentWriter paymentWriter;
    private final UserCommandUseCase pointUseCase;
    private final ConcertQueryUseCase getConcertUseCase;
    private final ConcertCommandUseCase reservationUseCase;
    private final TokenCommandUseCase tokenUseCase;

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
