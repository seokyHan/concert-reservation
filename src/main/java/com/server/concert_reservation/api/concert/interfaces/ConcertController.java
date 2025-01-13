package com.server.concert_reservation.api.concert.interfaces;

import com.server.concert_reservation.api.concert.application.ConcertQueryUseCase;
import com.server.concert_reservation.api.concert.application.ConcertCommandUseCase;
import com.server.concert_reservation.api.concert.domain.model.dto.ReservationCommand;
import com.server.concert_reservation.api.concert.interfaces.dto.ConcertHttp;
import com.server.concert_reservation.common.time.TimeManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Tag(name = "Concert", description = "Response Concert API")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ConcertController {

    private final ConcertQueryUseCase getConcertUseCase;
    private final ConcertCommandUseCase reservationUseCase;
    private final TimeManager timeManager;

    @GetMapping("/{concertId}/available-schedules")
    @Operation(summary = "예약 가능 콘서트 날짜 조회", description = "예약 가능한 콘서트 날짜 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 콘서트 날짜 조회 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertId", description = "콘서트 Id")
    public ResponseEntity<ConcertHttp.ConcertScheduleResponse> getAvailableSchedules(@PathVariable Long concertId, @RequestHeader("X-Waiting-Token") String token) {
        return ResponseEntity.ok(ConcertHttp.ConcertScheduleResponse.of(getConcertUseCase.getAvailableConcertSchedules(concertId, timeManager.now())));
    }

    @GetMapping("/concert-schedules/{concertScheduleId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회", description = "예약 가능한 좌석 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 좌석 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertScheduleId", description = "콘서트 스케쥴 Id")
    public ResponseEntity<ConcertHttp.ConcertSeatsResponse> getAvailableSeats(@PathVariable Long concertScheduleId, @RequestHeader("X-Waiting-Token") String token) {
        return ResponseEntity.ok(ConcertHttp.ConcertSeatsResponse.of(getConcertUseCase.getAvailableConcertSeats(concertScheduleId)));
    }

    @PostMapping("/concert-seats/reservation")
    @Operation(summary = "좌석 예약", description = "좌석 예약 요청 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좌석 예약 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ConcertHttp.ReservationResponse> reservationSeats(@RequestBody ConcertHttp.ConcertReservationRequest request, @RequestHeader("X-Waiting-Token") String token) {
        return ResponseEntity
                .status(CREATED)
                .body(ConcertHttp.ReservationResponse.of(reservationUseCase.temporaryReserveConcert(ReservationCommand.of(request))));
    }
}
