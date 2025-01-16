package com.server.concert_reservation.api.payment.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.concert_reservation.api.concert.application.ConcertQueryUseCase;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.payment.application.PaymentUseCase;
import com.server.concert_reservation.api.payment.application.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.application.dto.PaymentInfo;
import com.server.concert_reservation.api.payment.domain.model.Payment;
import com.server.concert_reservation.api.payment.domain.repository.PaymentWriter;
import com.server.concert_reservation.api.payment.interfaces.dto.PaymentHttp;
import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private TokenReader tokenReader;
    @MockitoBean
    private ConcertQueryUseCase concertQueryUseCase;
    @MockitoBean
    private PaymentWriter paymentWriter;
    @MockitoBean
    private TokenCommandUseCase tokenCommandUseCase;
    @MockitoBean
    private PaymentUseCase paymentUseCase;


    @Test
    @DisplayName("헤더에 토큰이 없으면 예외를 발생시킨다. - 결제 요청 API")
    void paymentMissingTokenThrowsExceptionTest() throws Exception {
        PaymentHttp.PaymentRequest request = new PaymentHttp.PaymentRequest(1L, 1L, "expired-token");

        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("만료시간이 지난 토큰으로 요청하면 예외를 발생시킨다. - 결제요청 API")
    void paymentExpiredTokenThrowsExceptionTest() throws Exception {
        PaymentHttp.PaymentRequest request = new PaymentHttp.PaymentRequest(1L, 1L, "expired-token");

        Token expiredToken = Token.builder()
                .token("expired-token")
                .activatedAt(LocalDateTime.now().minusMinutes(20))
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("X-Waiting-Token", "expired-token")
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 성공한다. - 결제 요청 API")
    void paymentValidTokenSuccess() throws Exception {
        PaymentHttp.PaymentRequest request = new PaymentHttp.PaymentRequest(1L, 1L, "expired-token");

        Long reservationId = 1L;
        Long userId = 1L;
        int totalPrice = 10000;

        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .totalPrice(totalPrice)
                .build();
        when(concertQueryUseCase.getReservation(reservationId)).thenReturn(reservation);

        Payment payment = Payment.builder()
                .reservationId(reservationId)
                .userId(userId)
                .amount(totalPrice)
                .build();
        when(paymentWriter.save(payment)).thenReturn(payment);

        Token expiredToken = Token.builder()
                .token("expired-token")
                .activatedAt(LocalDateTime.now())
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        PaymentCommand command = new PaymentCommand(userId, reservationId, "expired-token");
        PaymentInfo paymentInfo = new PaymentInfo(payment.getId(), userId, reservationId, totalPrice, LocalDateTime.now(), null);
        when(paymentUseCase.paymentReservation(command)).thenReturn(paymentInfo);


        mockMvc.perform(post("/api/v1/payment")
                        .header("X-Waiting-Token", "expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andDo(print());

        verify(tokenCommandUseCase, times(1)).checkActivatedToken("expired-token");
    }

}