package com.server.concert_reservation.interfaces.api.user;

import com.server.concert_reservation.interfaces.api.ApiResponseConverter;
import com.server.concert_reservation.interfaces.api.user.dto.UserHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "Response User API")
public class UserController {

    @GetMapping("/users/{userId}/balance")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "userId", description = "유저 Id")
    public ApiResponseConverter<UserHttp.UserBalanceResponse> getUserBalance(@PathVariable Long userId) {
        return ApiResponseConverter.ok(new UserHttp.UserBalanceResponse(new UserHttp.UserBalanceResponse.Balance(1L, 1L, 30000)));
    }

    @PatchMapping("/users/charge/balance")
    @Operation(summary = "금액 충전", description = "금액 충전 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<UserHttp.UserBalanceResponse> chargeBalance(@RequestBody UserHttp.UserBalanceRequest request) {
        return ApiResponseConverter.ok(new UserHttp.UserBalanceResponse(new UserHttp.UserBalanceResponse.Balance(1L, 1L, 30000)));
    }

    @PostMapping("/users/payment")
    @Operation(summary = "결제", description = "결제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ApiResponseConverter<UserHttp.UserPaymentResponse> payment(@RequestBody UserHttp.UserPaymentRequest request) {
        return ApiResponseConverter.ok(new UserHttp.UserPaymentResponse(new UserHttp.UserPaymentResponse.Payment(1L, 2L, 3L, 40000)));
    }
}
