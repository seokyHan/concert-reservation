package com.server.concert_reservation.interfaces.api.token;

import com.server.concert_reservation.interfaces.api.ApiResponseConverter;
import com.server.concert_reservation.interfaces.api.token.dto.TokenHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Token", description = "Response Token API")
public class TokenController {

    @PostMapping("/token")
    @Operation(summary = "대기열 토큰 발급", description = "대기열 토큰 발급 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<TokenHttp.TokenResponse> createToken(@RequestBody TokenHttp.TokenRequest request) {
        return ApiResponseConverter.ok(new TokenHttp.TokenResponse(new TokenHttp.TokenResponse.WaitingToken(1L, "fdfs-fda-dsfsd", 3L, "ACTIVE", LocalDateTime.now())));
    }

    @GetMapping("/token")
    @Operation(summary = "대기열 토큰 상태 조회", description = "대기열 토큰 상태 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<TokenHttp.TokenResponse> getTokenStatus(@RequestHeader("X-Waiting-Token") String token) {
        return ApiResponseConverter.ok(new TokenHttp.TokenResponse(new TokenHttp.TokenResponse.WaitingToken(1L, "fdfs-fda-dsfsd", 3L, "ACTIVE", LocalDateTime.now())));
    }
}
