package com.bulton.api_student_management.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final int MAX_BODY_LENGTH = 5000;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper =
            new ContentCachingRequestWrapper(request,MAX_BODY_LENGTH);

        ContentCachingResponseWrapper responseWrapper =
            new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            String method = request.getMethod();
            String uri = createRequestUri(request);
            int status = responseWrapper.getStatus();
            String username = getUsername(request);

            String requestBody = getRequestBody(requestWrapper);
            String responseBody = getResponseBody(responseWrapper);

            if (isSensitiveEndpoint(request.getRequestURI())) {
                requestBody = "[REDACTED]";
                responseBody = "[REDACTED]";
            }

            log.info(
                "API method={} uri={} status={} durationMs={} user={} requestBody={} responseBody={}",
                method,
                uri,
                status,
                duration,
                username,
                requestBody,
                responseBody
            );

            responseWrapper.copyBodyToResponse();
        }
    }

    private String createRequestUri(HttpServletRequest request) {
        String query = request.getQueryString();

        if (query == null || query.isBlank()) {
            return request.getRequestURI();
        }

        return request.getRequestURI() + "?" + query;
    }

    private String getUsername(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "anonymous";
        }

        return request.getUserPrincipal().getName();
    }

    private String getRequestBody(
        ContentCachingRequestWrapper request
    ) {
        byte[] content = request.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return limit(new String(content, StandardCharsets.UTF_8));
    }

    private String getResponseBody(
        ContentCachingResponseWrapper response
    ) {
        byte[] content = response.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return limit(new String(content, StandardCharsets.UTF_8));
    }

    private String limit(String body) {
        String singleLine = body
            .replace("\n", "")
            .replace("\r", "");

        if (singleLine.length() <= MAX_BODY_LENGTH) {
            return singleLine;
        }

        return singleLine.substring(0, MAX_BODY_LENGTH) + "...[TRUNCATED]";
    }

    private boolean isSensitiveEndpoint(String uri) {
        return uri.equals("/api/auth/login")
            || uri.equals("/api/auth/register");
    }
}