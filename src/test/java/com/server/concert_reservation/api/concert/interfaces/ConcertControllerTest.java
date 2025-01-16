package com.server.concert_reservation.api.concert.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.concert_reservation.api.concert.application.ConcertQueryUseCase;
import com.server.concert_reservation.api.concert.application.dto.ConcertSeatInfo;
import com.server.concert_reservation.api.concert.application.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.model.ConcertSeat;
import com.server.concert_reservation.api.concert.domain.model.Reservation;
import com.server.concert_reservation.api.concert.domain.repository.ConcertReader;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.api.concert.interfaces.dto.ConcertHttp;
import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.token.domain.model.Token;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.api.token.infrastructure.entity.types.TokenStatus;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConcertControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    @MockitoBean
    private TokenCommandUseCase tokenCommandUseCase;
    @MockitoBean
    private ConcertQueryUseCase concertQueryUseCase;
    @MockitoBean
    private ConcertReader concertReader;
    @MockitoBean
    private ConcertWriter concertWriter;
    @MockitoBean
    private TokenReader tokenReader;
    @MockitoBean
    private TimeManager timeManager;

    @BeforeEach
    void dataBaseCleansing() {
        when(timeManager.now()).thenReturn(LocalDateTime.now());
    }

    @Test
    @DisplayName("헤더에 토큰이 없으면 예외를 발생시킨다. - 예약 가능한 콘서트 날짜 조회 API")
    void getAvailableSchedulesMissingTokenThrowsExceptionTest() throws Exception {
        mockMvc.perform(get("/api/v1/concert/1/available-schedules"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("만료시간이 지난 토큰으로 요청하면 예외를 발생시킨다. - 예약 가능한 콘서트 날짜 조회 API")
    void getAvailableSchedulesExpiredTokenThrowsExceptionTest() throws Exception {
        Token expiredToken = Token.builder()
                .token("expired-token")
                .activatedAt(LocalDateTime.now().minusMinutes(20))
                .build();

        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        mockMvc.perform(get("/api/v1/concert/1/available-schedules")
                        .header("X-Waiting-Token", "expired-token"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 성공한다. - 예약 가능한 콘서트 날짜 조회 API")
    void getAvailableSchedulesValidTokenSuccess() throws Exception {
        Token expiredToken = Token.builder()
                .token("expired-token")
                .status(TokenStatus.EXPIRED)
                .activatedAt(LocalDateTime.now())
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        mockMvc.perform(get("/api/v1/concert/1/available-schedules")
                        .header("X-Waiting-Token", "expired-token"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(tokenCommandUseCase, times(1)).checkActivatedToken("expired-token");
    }

    @Test
    @DisplayName("헤더에 토큰이 없으면 예외를 발생시킨다. - 예약 가능한 좌석 조회 API")
    void getAvailableSeatsMissingTokenThrowsExceptionTest() throws Exception {
        mockMvc.perform(get("/api/v1/concert/1//available-seats"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("만료시간이 지난 토큰으로 요청하면 예외를 발생시킨다. - 예약 가능한 좌석 조회 API")
    void getAvailableSeatsExpiredTokenThrowsExceptionTest() throws Exception {
        Token expiredToken = Token.builder()
                .token("expired-token")
                .activatedAt(LocalDateTime.now().minusMinutes(20))
                .build();

        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        mockMvc.perform(get("/api/v1/concert/1/available-seats")
                        .header("X-Waiting-Token", "expired-token"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 성공한다. - 예약 가능한 좌석 조회 API")
    void getAvailableSeatsValidTokenSuccess() throws Exception {
        Token expiredToken = Token.builder()
                .token("expired-token")
                .status(TokenStatus.EXPIRED)
                .activatedAt(LocalDateTime.now())
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);
        ConcertSeatInfo concertSeatInfo = mock(ConcertSeatInfo.class);
        when(concertQueryUseCase.getAvailableConcertSeats(1L)).thenReturn(concertSeatInfo);

        mockMvc.perform(get("/api/v1/concert/1/available-seats")
                        .header("X-Waiting-Token", "expired-token"))
                .andExpect(status().isOk())
                .andDo(print());

        verify(tokenCommandUseCase, times(1)).checkActivatedToken("expired-token");
    }

    @Test
    @DisplayName("헤더에 토큰이 없으면 예외를 발생시킨다. - 좌석 예약 요청 API")
    void reservationSeatsMissingTokenThrowsExceptionTest() throws Exception {
        ConcertHttp.ConcertReservationRequest request = new ConcertHttp.ConcertReservationRequest(1L, 1L, List.of(1L,2L), LocalDateTime.now());

        mockMvc.perform(post("/api/v1/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @DisplayName("만료시간이 지난 토큰으로 요청하면 예외를 발생시킨다. - 좌석 예약 요청 API")
    void reservationSeatsExpiredTokenThrowsExceptionTest() throws Exception {
        ConcertHttp.ConcertReservationRequest request = new ConcertHttp.ConcertReservationRequest(1L, 1L, List.of(1L,2L), LocalDateTime.now());

        Token expiredToken = Token.builder()
                .token("expired-token")
                .activatedAt(LocalDateTime.now().minusMinutes(20))
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        mockMvc.perform(post("/api/v1/concert/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("X-Waiting-Token", "expired-token")
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("유효한 토큰으로 요청하면 성공한다. - 좌석 예약 요청 API")
    void reservationSeatsValidTokenSuccess() throws Exception {
        ConcertHttp.ConcertReservationRequest request = new ConcertHttp.ConcertReservationRequest(1L, 1L, List.of(1L), LocalDateTime.now());

        Token expiredToken = Token.builder()
                .token("expired-token")
                .status(TokenStatus.EXPIRED)
                .activatedAt(LocalDateTime.now())
                .build();
        when(tokenReader.getByToken("expired-token")).thenReturn(expiredToken);

        ConcertSchedule concertSchedule = mock(ConcertSchedule.class);
        when(concertReader.getConcertScheduleById(1L)).thenReturn(concertSchedule);

        ConcertSeat concertSeat = mock(ConcertSeat.class);
        when(concertReader.getConcertSeatById(1L)).thenReturn(concertSeat);

        ReservationCommand command = new ReservationCommand(1L, 1L, List.of(1L, 2L), LocalDateTime.now());
        Reservation concertReservation = Reservation.createReservation(command, 30000, timeManager.now());
        when(concertWriter.saveAll(anyList())).thenReturn(List.of(concertSeat));
        when(concertWriter.saveReservation(any(Reservation.class))).thenReturn(concertReservation);


        mockMvc.perform(post("/api/v1/concert/reservation")
                        .header("X-Waiting-Token", "expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andDo(print());

        verify(tokenCommandUseCase, times(1)).checkActivatedToken("expired-token");
    }


}