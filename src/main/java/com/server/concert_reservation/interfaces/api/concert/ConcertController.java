package com.server.concert_reservation.interfaces.api.concert;

import com.server.concert_reservation.interfaces.api.ApiResponseConverter;
import com.server.concert_reservation.interfaces.api.concert.dto.ConcertHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;


import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Concert", description = "Response Concert API")
public class ConcertController {

    @GetMapping("/concert/list")
    @Operation(summary = "콘서트 목록 조회", description = "콘서트 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<ConcertHttp.ConcertListResponse> getAvailableSchedules() {
        return ApiResponseConverter.ok(new ConcertHttp.ConcertListResponse(List.of(new ConcertHttp.ConcertListResponse.ConcertList(1L, "제목1", "설명1", LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()))));
    }

    @GetMapping("/{concertId}/available-schedules")
    @Operation(summary = "예약 가능 콘서트 날짜 조회", description = "예약 가능한 콘서트 날짜 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertId", description = "콘서트 Id")
    public ApiResponseConverter<ConcertHttp.ConcertScheduleResponse> getAvailableSchedules(@PathVariable Long concertId, @RequestHeader("X-Waiting-Token") String token) {
        return ApiResponseConverter.ok(new ConcertHttp.ConcertScheduleResponse(List.of(new ConcertHttp.ConcertScheduleResponse.ConcertSchedules(1L, 1L, LocalDateTime.now(), LocalDate.now().plusDays(1).atStartOfDay()))));
    }

    @GetMapping("/concert-schedules/{concertScheduleId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회", description = "예약 가능한 좌석 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertScheduleId", description = "콘서트 스케쥴 Id")
    public ApiResponseConverter<ConcertHttp.ConcertSeatsResponse> getAvailableSeats(@PathVariable Long concertScheduleId, @RequestHeader("X-Waiting-Token") String token) {
        return ApiResponseConverter.ok(new ConcertHttp.ConcertSeatsResponse(new ConcertHttp.ConcertSeatsResponse.ConcertSeats(1L, 1L, 20, 30000, false)));
    }

    @PostMapping("/concert-seats/reservation")
    @Operation(summary = "좌석 예약", description = "좌석 예약 요청 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<ConcertHttp.ConcertReservationResponse> reservationSeats(@RequestBody ConcertHttp.ConcertReservationRequest request, @RequestHeader("X-Waiting-Token") String token) {
        return ApiResponseConverter.created(new ConcertHttp.ConcertReservationResponse(new ConcertHttp.ConcertReservationResponse.ConcertReservation(1L, 1L, 3L, "RESERVED")));
    }
}
