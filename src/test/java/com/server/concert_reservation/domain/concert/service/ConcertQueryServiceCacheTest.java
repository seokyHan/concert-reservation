package com.server.concert_reservation.domain.concert.service;

import com.server.concert_reservation.domain.concert.dto.ConcertScheduleInfo;
import com.server.concert_reservation.domain.concert.dto.ConcertSeatInfo;
import com.server.concert_reservation.domain.concert.model.ConcertSchedule;
import com.server.concert_reservation.domain.concert.model.ConcertSeat;
import com.server.concert_reservation.domain.concert.repository.ConcertReader;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
public class ConcertQueryServiceCacheTest {

    @Autowired
    private ConcertQueryService concertQueryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @MockitoBean
    private ConcertReader concertReader;

    @DisplayName("콘서트 예약 가능 스케쥴 조회 캐시 테스트 - 1번만 DB조회 되는지 검증")
    @Test
    public void findAvailableConcertSchedulesCacheableTest() {
        Long concertId = 1L;
        LocalDateTime now = LocalDateTime.now();

        List<ConcertSchedule> mockResult = List.of(Instancio.create(ConcertSchedule.class));
        when(concertReader.getConcertScheduleByConcertId(concertId)).thenReturn(mockResult);

        // 첫 번째 호출 (DB 조회 발생해야 함)
        List<ConcertScheduleInfo> firstCall = concertQueryService.findAvailableConcertSchedules(concertId, now);
        verify(concertReader, times(1)).getConcertScheduleByConcertId(concertId);

        // When - 두 번째 호출 (캐시에서 가져와야 함, DB 조회 X) 두 번째 호출에서는 캐시에서 가져와야 하므로 DB 호출 횟수 증가 X
        List<ConcertScheduleInfo> secondCall = concertQueryService.findAvailableConcertSchedules(concertId, now);
        verify(concertReader, times(1)).getConcertScheduleByConcertId(concertId);


        assertEquals(firstCall, secondCall);
    }

    @Test
    public void findAvailableConcertSeatsCacheableTest() {
        Long concertScheduleId = 100L;

        List<ConcertSeat> mockSeats = List.of(Instancio.create(ConcertSeat.class));
        when(concertReader.getConcertSeatByScheduleId(concertScheduleId)).thenReturn(mockSeats);

        List<ConcertSeatInfo> firstCall = concertQueryService.findAvailableConcertSeats(concertScheduleId);
        verify(concertReader, times(1)).getConcertSeatByScheduleId(concertScheduleId);

        List<ConcertSeatInfo> secondCall = concertQueryService.findAvailableConcertSeats(concertScheduleId);
        verify(concertReader, times(1)).getConcertSeatByScheduleId(concertScheduleId);

        assertEquals(firstCall, secondCall);
    }

}