package com.server.concert_reservation.interfaces.web.waitingqueue;

import com.server.concert_reservation.application.waitingqueue.WaitingQueueUseCase;
import com.server.concert_reservation.interfaces.web.waitingqueue.dto.WaitingQueueHttpResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@Tag(name = "Token", description = "Response Token API")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WaitingQueueController {

    private final WaitingQueueUseCase queueTokenUseCase;

    @PostMapping("/token/create")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰 발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대기열 토큰 발급 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<WaitingQueueHttpResponse.WaitingQueueResponse> createToken() {
        return ResponseEntity
                .status(CREATED)
                .body(WaitingQueueHttpResponse.WaitingQueueResponse.from(queueTokenUseCase.issueWaitingQueueToken()));
    }

    @GetMapping("/token")
    @Operation(summary = "대기열 토큰 상태 조회", description = "대기열 토큰 상태 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대기열 토큰 상태 조회 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<WaitingQueueHttpResponse.WaitingQueuePositionResponse> getWaitingQueuePosition(
            @RequestHeader("X-Waiting-Token") String token
    ) {
        return ResponseEntity.ok(WaitingQueueHttpResponse.WaitingQueuePositionResponse.from(queueTokenUseCase.getWaitingQueuePosition(token)));
    }
}
