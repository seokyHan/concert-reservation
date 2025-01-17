package com.server.concert_reservation.api.user.interfaces;

import com.server.concert_reservation.api.user.application.dto.UserCommand;
import com.server.concert_reservation.api.user.application.UserCommandService;
import com.server.concert_reservation.api.user.application.UserQueryService;
import com.server.concert_reservation.api.user.interfaces.dto.UserHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "User", description = "Response User API")
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UserController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;

    @GetMapping("/users/{userId}/wallet")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "userId", description = "유저 Id")
    public ResponseEntity<UserHttp.UserWalletResponse> getUserWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(UserHttp.UserWalletResponse.of(userQueryService.getWallet(userId)));
    }

    @PatchMapping("/users/charge/point")
    @Operation(summary = "포인트 충전", description = "포인트 충전 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserHttp.UserWalletResponse> chargePoint(@RequestBody UserHttp.UserWalletRequest request) {
        return ResponseEntity.ok(UserHttp.UserWalletResponse.of(userCommandService.chargePoint(UserCommand.of(request))));
    }

}


