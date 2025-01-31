package com.server.concert_reservation.interfaces.web.payment;

import com.server.concert_reservation.application.payment.dto.PaymentCommand;
import com.server.concert_reservation.interfaces.web.payment.dto.PaymentHttpRequest;
import com.server.concert_reservation.interfaces.web.payment.dto.PaymentHttpResponse;
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
    public ResponseEntity<PaymentHttpResponse.PaymentResponse> payment(@RequestBody PaymentHttpRequest.PaymentRequest request, @RequestHeader("X-Waiting-Token") String token) {
        return ResponseEntity.ok(PaymentHttpResponse.PaymentResponse.of(paymentUseCase.paymentReservation(PaymentCommand.from(request, token))));
    }
}
