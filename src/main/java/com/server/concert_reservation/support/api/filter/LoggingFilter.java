package com.server.concert_reservation.support.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            logRequest(wrappedRequest);
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            logResponse(wrappedResponse);
            wrappedResponse.copyBodyToResponse();
        }

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        val excludePaths = List.of("/swagger-ui");
        return excludePaths.stream()
                .anyMatch(request.getRequestURI()::startsWith);
    }

    private void logRequest(ContentCachingRequestWrapper request) {
        String uri = request.getRequestURI();
        String body = getContentAsString(request.getContentAsByteArray(), request.getCharacterEncoding());
        log.info("Request URI: {}, Request Body: {}", uri, body);
    }

    private void logResponse(ContentCachingResponseWrapper response) {
        int status = response.getStatus();
        String body = getContentAsString(response.getContentAsByteArray(), response.getCharacterEncoding());
        log.info("Response Status: {}, Response Body: {}", status, body);
    }

    private String getContentAsString(byte[] content, String encoding) {
        try {
            return content.length > 0 ? new String(content, encoding) : "";
        } catch (UnsupportedEncodingException e) {
            log.warn("encoding error : {}", encoding, e);
            return "encoding error";
        }
    }
}
