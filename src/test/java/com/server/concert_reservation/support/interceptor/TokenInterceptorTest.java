package com.server.concert_reservation.support.interceptor;

import com.server.concert_reservation.domain.queue_token.dto.QueueTokenInfo;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenCommandService;
import com.server.concert_reservation.domain.queue_token.service.QueueTokenQueryService;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import com.server.concert_reservation.support.api.interceptor.TokenInterceptor;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;

import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.TOKEN_EXPIRED;
import static com.server.concert_reservation.domain.queue_token.errorcode.QueueTokenErrorCode.TOKEN_NOT_FOUND;
import static com.server.concert_reservation.infrastructure.queue_token.entity.types.QueueTokenStatus.ACTIVE;
import static org.instancio.Select.field;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenInterceptorTest {

    @InjectMocks
    private TokenInterceptor tokenInterceptor;

    @Mock
    private QueueTokenQueryService queueTokenQueryService;

    @Mock
    private QueueTokenCommandService queueTokenCommandService;

    @Mock
    private TimeManager timeManager;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private HandlerMethod handler;

    @BeforeEach
    void setUp() throws NoSuchMethodException {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        handler = new HandlerMethod(this, this.getClass().getDeclaredMethod("mockHandler"));
    }

    @DisplayName("헤더에 토큰이 없는 경우 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenHeaderDoNotHaveToken() {
        // Given
        request.removeHeader("X-Waiting-Token");

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                tokenInterceptor.preHandle(request, response, handler)
        );
        assertEquals(TOKEN_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @DisplayName("토큰이 만료된 경우 예외가 발생한다.")
    @Test
    void shouldThrownExceptionWhenTokenExpired() {
        // Given
        String tokenValue = "test-token";
        request.addHeader("X-Waiting-Token", tokenValue);
        LocalDateTime activatedAt = LocalDateTime.now().minusMinutes(11);
        QueueTokenInfo token = Instancio.of(QueueTokenInfo.class)
                .set(field(QueueTokenInfo::activatedAt), activatedAt)
                .create();

        when(queueTokenQueryService.findQueueToken(tokenValue)).thenReturn(token);
        when(timeManager.now()).thenReturn(LocalDateTime.now());

        // When & Then
        CustomException exception = assertThrows(CustomException.class, () ->
                tokenInterceptor.preHandle(request, response, handler)
        );

        assertEquals(TOKEN_EXPIRED.getMessage(), exception.getMessage());
        verify(queueTokenCommandService).expireToken(tokenValue);
    }

    @DisplayName("토큰 인터셉터에 정상적으로 통과한다.")
    @Test
    void successPassTokenInterceptor() throws Exception {
        // Given
        String tokenValue = "valid-token";
        request.addHeader("X-Waiting-Token", tokenValue);
        LocalDateTime now = LocalDateTime.now();
        QueueTokenInfo token = Instancio.of(QueueTokenInfo.class)
                .set(field(QueueTokenInfo::status), ACTIVE)
                .set(field(QueueTokenInfo::activatedAt), now)
                .create();

        when(queueTokenQueryService.findQueueToken(tokenValue)).thenReturn(token);
        when(timeManager.now()).thenReturn(now);

        // When
        boolean result = tokenInterceptor.preHandle(request, response, handler);

        // Then
        assertTrue(result);
        verify(queueTokenCommandService).checkActivatedToken(tokenValue);
        verify(queueTokenCommandService, never()).expireToken(anyString());
    }

    // 테스트용 빈 핸들러 메서드
    private void mockHandler() {
    }
}
