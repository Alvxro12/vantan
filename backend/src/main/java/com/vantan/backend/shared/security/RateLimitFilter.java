package com.vantan.backend.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vantan.backend.shared.response.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String LOGIN_PATH      = "/api/auth/login";
    private static final int    MAX_ATTEMPTS    = 5;
    private static final long   WINDOW_MINUTES  = 15;
    private static final String RL_IP_PREFIX    = "rate_limit:ip:";
    private static final String RL_EMAIL_PREFIX = "rate_limit:email:";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (!LOGIN_PATH.equals(request.getRequestURI())
                || !"POST".equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = request.getRemoteAddr();
        String ipKey = RL_IP_PREFIX + ip;

        if (isBlocked(ipKey)) {
            writeBlockedResponse(response, "Too many login attempts. Try again in 15 minutes.");
            return;
        }

        filterChain.doFilter(request, response);

        // Si la respuesta fue 401 o 400 → incrementar contador
        int status = response.getStatus();
        if (status == HttpStatus.BAD_REQUEST.value()
                || status == HttpStatus.UNAUTHORIZED.value()) {
            increment(ipKey);
            log.warn("Failed login attempt from IP: {}", ip);
        }
    }

    private boolean isBlocked(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) return false;
        return Integer.parseInt(value) >= MAX_ATTEMPTS;
    }

    private void increment(String key) {
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1) {
            redisTemplate.expire(key, WINDOW_MINUTES, TimeUnit.MINUTES);
        }
    }

    private void writeBlockedResponse(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.error(message, "RATE_LIMIT_EXCEEDED")
                )
        );
    }
}