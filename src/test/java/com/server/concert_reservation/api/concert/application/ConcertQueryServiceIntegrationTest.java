package com.server.concert_reservation.api.concert.application;

import com.server.concert_reservation.api.concert.application.dto.ConcertScheduleInfo;
import com.server.concert_reservation.api.concert.domain.model.Concert;
import com.server.concert_reservation.api.concert.domain.model.ConcertSchedule;
import com.server.concert_reservation.api.concert.domain.repository.ConcertWriter;
import com.server.concert_reservation.support.DatabaseCleanUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ConcertQueryServiceIntegrationTest {

    @Autowired
    private ConcertQueryUseCase getConcertUseCase;
    @Autowired
    private ConcertWriter concertWriter;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void dataBaseCleansing() {
        databaseCleanUp.execute();
    }

    @DisplayName("예약 가능한 콘서트 스케쥴 조회")
    @Test
    void getAvailableConcertSchedulesTest() {
        LocalDateTime now = LocalDateTime.now();
        // given
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

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = getConcertUseCase.getAvailableConcertSchedules(saveConcertSchedule.getConcertId(), now);

        // then
        assertAll(
                () -> assertEquals(1, availableConcertSchedule.size()),
                () -> assertEquals(10, availableConcertSchedule.get(0).remainTicket())
        );
    }

    @DisplayName("예약 가능 시기가 아닌 날짜로 조회하면 빈리스트가 반환된다.")
    @Test
    void notAvailableConcertSchedulesTobeEmptyList() {
        LocalDateTime now = LocalDateTime.now();
        // given
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

        // when
        List<ConcertScheduleInfo> availableConcertSchedule = getConcertUseCase.getAvailableConcertSchedules(saveConcertSchedule.getConcertId(), now.plusDays(2L));

        // then
        assertAll(
                () -> assertEquals(0, availableConcertSchedule.size())
        );
    }


}