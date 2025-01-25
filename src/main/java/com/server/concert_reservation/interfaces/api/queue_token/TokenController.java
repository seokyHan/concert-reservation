package com.server.concert_reservation.interfaces.api.queue_token;

import com.server.concert_reservation.api_backup.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api_backup.token.application.TokenQueryUseCase;
import com.server.concert_reservation.api_backup.token.application.dto.TokenCommand;
import com.server.concert_reservation.interfaces.api.queue_token.dto.QueueTokenHttp;
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
public class TokenController {

    private final TokenCommandUseCase tokenCommandUseCase;
    private final TokenQueryUseCase tokenQueryUseCase;

    @PostMapping("/token/create")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰 발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "대기열 토큰 발급 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<QueueTokenHttp.QueueTokenResponse> createToken(@RequestBody QueueTokenHttp.QueueTokenRequest request) {
        return ResponseEntity
                .status(CREATED)
                .body(QueueTokenHttp.QueueTokenResponse.of(tokenCommandUseCase.createToken(TokenCommand.of(request))));
    }

    @GetMapping("/token")
    @Operation(summary = "대기열 토큰 상태 조회", description = "대기열 토큰 상태 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대기열 토큰 상태 조회 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<QueueTokenHttp.QueueTokenResponse> getTokenStatus(
            @RequestHeader("X-Waiting-Token") String token,
            @RequestHeader("USER-ID") Long userId
    ) {
        return ResponseEntity.ok(QueueTokenHttp.QueueTokenResponse.of(tokenQueryUseCase.getWaitingToken(token, userId)));
    }
}
