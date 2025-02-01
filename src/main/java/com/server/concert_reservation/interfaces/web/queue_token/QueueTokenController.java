package com.server.concert_reservation.interfaces.web.queue_token;

import com.server.concert_reservation.application.queue_token.QueueTokenUseCase;
import com.server.concert_reservation.application.queue_token.dto.QueueTokenCommand;
import com.server.concert_reservation.interfaces.web.queue_token.dto.QueueTokenHttpRequest;
import com.server.concert_reservation.interfaces.web.queue_token.dto.QueueTokenHttpResponse;
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
public class QueueTokenController {

    private final QueueTokenUseCase queueTokenUseCase;

    @PostMapping("/token/create")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰 발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대기열 토큰 발급 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<QueueTokenHttpResponse.QueueTokenResponse> createToken(@RequestBody QueueTokenHttpRequest.QueueTokenRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(QueueTokenHttpResponse.QueueTokenResponse.of(queueTokenUseCase.issueToken(QueueTokenCommand.of(request))));
    }

    @GetMapping("/token")
    @Operation(summary = "대기열 토큰 상태 조회", description = "대기열 토큰 상태 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대기열 토큰 상태 조회 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<QueueTokenHttpResponse.QueueTokenResponse> getTokenStatus(
            @RequestHeader("X-Waiting-Token") String token,
            @RequestHeader("USER-ID") Long userId
    ) {
        return ResponseEntity.ok(QueueTokenHttpResponse.QueueTokenResponse.of(queueTokenUseCase.getWaitingToken(token, userId)));
    }
}
