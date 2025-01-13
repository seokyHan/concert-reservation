package com.server.concert_reservation.api.payment.presentation;

import com.server.concert_reservation.api.payment.application.PaymentUseCase;
import com.server.concert_reservation.api.payment.domain.model.dto.PaymentCommand;
import com.server.concert_reservation.api.payment.presentation.dto.PaymentHttp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Response Payment API")
@RequestMapping("/api/v1")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    @PostMapping("/payment")
    @Operation(summary = "결제", description = "결제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "결제 요청 성공.", content = @Content(mediaType = "application/json")),
    })
    public ResponseEntity<PaymentHttp.PaymentResponse> payment(@RequestBody PaymentHttp.PaymentRequest request, @RequestHeader("X-Waiting-Token") String token) {
        return ResponseEntity.ok(PaymentHttp.PaymentResponse.of(paymentUseCase.paymentReservation(PaymentCommand.from(request, token))));
    }
}
