package com.server.concert_reservation.interfaces.web.concert;

import com.server.concert_reservation.application.concert.ConcertUseCase;
import com.server.concert_reservation.application.concert.dto.ReservationCommand;
import com.server.concert_reservation.interfaces.web.concert.dto.ConcertHttpRequest;
import com.server.concert_reservation.interfaces.web.concert.dto.ConcertHttpResponse;
import com.server.concert_reservation.support.api.common.time.TimeManager;
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
@RequestMapping("/api/v1/concert")
public class ConcertController {

    private final ConcertUseCase concertFacade;
    private final TimeManager timeManager;

    @GetMapping("/{concertId}/available-schedules")
    @Operation(summary = "예약 가능 콘서트 날짜 조회", description = "예약 가능한 콘서트 날짜 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 콘서트 날짜 조회 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertId", description = "콘서트 Id")
    public ResponseEntity<ConcertHttpResponse.ConcertScheduleResponse> getAvailableSchedules(@PathVariable Long concertId) {
        return ResponseEntity.ok(ConcertHttpResponse.ConcertScheduleResponse.of(concertFacade.getAvailableConcertSchedules(concertId, timeManager.now())));
    }

    @GetMapping("/{concertScheduleId}/available-seats")
    @Operation(summary = "예약 가능 좌석 조회", description = "예약 가능한 좌석 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 가능 좌석 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "concertScheduleId", description = "콘서트 스케쥴 Id")
    public ResponseEntity<ConcertHttpResponse.ConcertSeatsResponse> getAvailableSeats(@PathVariable Long concertScheduleId) {

        return ResponseEntity.ok(ConcertHttpResponse.ConcertSeatsResponse.of(concertFacade.getAvailableConcertSeats(concertScheduleId)));
    }

    @PostMapping("/reservation")
    @Operation(summary = "좌석 예약", description = "좌석 예약 요청 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좌석 예약 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<ConcertHttpResponse.ReservationResponse> reservationSeats(@RequestBody ConcertHttpRequest.ConcertReservationRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(ConcertHttpResponse.ReservationResponse.of(concertFacade.reserveSeats(ReservationCommand.of(request))));
    }
}
