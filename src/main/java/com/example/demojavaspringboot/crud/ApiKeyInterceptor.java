package com.example.demojavaspringboot.crud;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${app.api.key:my-secret-api-key}")
    private String apiKey;

    @Value("${app.api.key-header:X-API-KEY}")
    private String apiKeyHeader;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestApiKey = request.getHeader(apiKeyHeader);

        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Neplatný nebo chybějící API klíč.\"}");
            return false;
        }

        return true;
    }
}
