package com.server.concert_reservation.interfaces.web.support.interceptor;

import com.server.concert_reservation.application.waitingqueue.WaitingQueueUseCase;
import com.server.concert_reservation.interfaces.web.support.exception.CustomException;
import com.server.concert_reservation.interfaces.web.support.time.TimeManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.server.concert_reservation.domain.waitingqueue.errorcode.WaitingQueueErrorCode.WAITING_QUEUE_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final WaitingQueueUseCase waitingQueueUseCase;
    private final TimeManager timeManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val requestToken = request.getHeader("X-Waiting-Token");
        // 헤더 토큰 유무 검증
        if (StringUtils.isBlank(requestToken)) {
            throw new CustomException(WAITING_QUEUE_NOT_FOUND);
        }

        waitingQueueUseCase.validateWaitingQueueProcessing(requestToken);

        return true;
    }
}
