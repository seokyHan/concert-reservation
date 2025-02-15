package com.server.concert_reservation.interfaces.web.user;

import com.server.concert_reservation.application.user.UserUseCase;
import com.server.concert_reservation.application.user.dto.UserCommand;
import com.server.concert_reservation.interfaces.web.user.dto.UserHttpRequest;
import com.server.concert_reservation.interfaces.web.user.dto.UserHttpResponse;
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

    private final UserUseCase userUseCase;

    @GetMapping("/users/{userId}/wallet")
    @Operation(summary = "잔액 조회", description = "잔액 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    @Parameter(name = "userId", description = "유저 Id")
    public ResponseEntity<UserHttpResponse.UserWalletResponse> getUserWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(UserHttpResponse.UserWalletResponse.of(userUseCase.getWallet(userId)));
    }

    @PatchMapping("/users/charge/point")
    @Operation(summary = "포인트 충전", description = "포인트 충전 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<UserHttpResponse.UserWalletResponse> chargePoint(@RequestBody UserHttpRequest.UserWalletRequest request) {
        return ResponseEntity.ok(UserHttpResponse.UserWalletResponse.of(userUseCase.chargePoint(UserCommand.from(request))));
    }

}


