package com.server.concert_reservation.support.api.interceptor;

import com.server.concert_reservation.api.token.application.TokenCommandUseCase;
import com.server.concert_reservation.api.token.domain.repository.TokenReader;
import com.server.concert_reservation.support.api.common.exception.CustomException;
import com.server.concert_reservation.support.api.common.time.TimeManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

import static com.server.concert_reservation.api.token.domain.errorcode.TokenErrorCode.TOKEN_EXPIRED;
import static com.server.concert_reservation.api.token.domain.errorcode.TokenErrorCode.TOKEN_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class TokenInterceptor implements HandlerInterceptor {

    private final TokenCommandUseCase tokenCommandUseCase;
    private final TokenReader tokenReader;
    private final TimeManager timeManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        val requestToken = request.getHeader("X-Waiting-Token");
        // 헤더 토큰 유무 검증
        if(StringUtils.isBlank(requestToken)) {
            throw new CustomException(TOKEN_NOT_FOUND);
        }

        // 토큰 만료 스케쥴러와 타이밍이 어긋날 수 있기 때문에 토큰 만료 여부 검증
        val token = tokenReader.getByToken(requestToken);
        if(isActivatedAtExceed(token.getActivatedAt())) {
            tokenCommandUseCase.expireToken(requestToken);
            throw new CustomException(TOKEN_EXPIRED);
        }

        tokenCommandUseCase.checkActivatedToken(requestToken);

        return true;
    }

    private boolean isActivatedAtExceed(LocalDateTime activatedAt) {
        return activatedAt.isBefore(timeManager.now().minusMinutes(10));
    }
}
