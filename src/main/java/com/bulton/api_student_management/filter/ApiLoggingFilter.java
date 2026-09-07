package com.bulton.api_student_management.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApiLoggingFilter extends OncePerRequestFilter {

    private static final Logger API_LOG =
        LoggerFactory.getLogger("API_AUDIT");

    private static final int MAX_BODY_LENGTH = 5000;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper =
            new ContentCachingRequestWrapper(
                request,
                MAX_BODY_LENGTH
            );

        ContentCachingResponseWrapper responseWrapper =
            new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        try {
            filterChain.doFilter(
                requestWrapper,
                responseWrapper
            );
        } finally {
            long duration =
                System.currentTimeMillis() - startTime;

            try {
                writeJsonLog(
                    requestWrapper,
                    responseWrapper,
                    duration
                );
            } catch (Exception loggingException) {
                log.error(
                    "Failed to create API audit log",
                    loggingException
                );
            } finally {
                // Always copy the cached response back to Postman.
                responseWrapper.copyBodyToResponse();
            }
        }
    }

    private void writeJsonLog(
        ContentCachingRequestWrapper request,
        ContentCachingResponseWrapper response,
        long duration
    ) throws Exception {

        String method = request.getMethod();
        String uri = createRequestUri(request);
        String username = getUsername(request);

        String requestBody = getRequestBody(request);
        String responseBody = getResponseBody(response);

        ObjectNode logEntry = objectMapper.createObjectNode();

        logEntry.put("event", "API_CALL");
        logEntry.put("method", method);
        logEntry.put("uri", uri);
        logEntry.put("status", response.getStatus());
        logEntry.put("durationMs", duration);
        logEntry.put("user", username);

        if (isWriteMethod(method)) {
            logEntry.put("performedBy", username);
        }

        if (isSensitiveEndpoint(request.getRequestURI())) {
            logEntry.put("request", "[REDACTED]");
            logEntry.put("response", "[REDACTED]");
        } else {
            addJsonOrText(
                logEntry,
                "request",
                requestBody
            );

            addJsonOrText(
                logEntry,
                "response",
                responseBody
            );
        }

        API_LOG.info(
            objectMapper.writeValueAsString(logEntry)
        );
    }

    private void addJsonOrText(
        ObjectNode logEntry,
        String field,
        String content
    ) {
        if (content == null || content.isBlank()) {
            logEntry.putNull(field);
            return;
        }

        try {
            logEntry.set(
                field,
                objectMapper.readTree(content)
            );
        } catch (Exception exception) {
            logEntry.put(field, content);
        }
    }

    private boolean isWriteMethod(String method) {
        return method.equalsIgnoreCase("POST")
            || method.equalsIgnoreCase("PUT")
            || method.equalsIgnoreCase("PATCH")
            || method.equalsIgnoreCase("DELETE");
    }

    private String createRequestUri(
        HttpServletRequest request
    ) {
        String query = request.getQueryString();

        if (query == null || query.isBlank()) {
            return request.getRequestURI();
        }

        return request.getRequestURI() + "?" + query;
    }

    private String getUsername(
        HttpServletRequest request
    ) {
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

        return limit(
            new String(content, StandardCharsets.UTF_8)
        );
    }

    private String getResponseBody(
        ContentCachingResponseWrapper response
    ) {
        byte[] content = response.getContentAsByteArray();

        if (content.length == 0) {
            return "";
        }

        return limit(
            new String(content, StandardCharsets.UTF_8)
        );
    }

    private String limit(String body) {
        String singleLine = body
            .replace("\n", "")
            .replace("\r", "");

        if (singleLine.length() <= MAX_BODY_LENGTH) {
            return singleLine;
        }

        return singleLine.substring(
            0,
            MAX_BODY_LENGTH
        ) + "...[TRUNCATED]";
    }

    private boolean isSensitiveEndpoint(String uri) {
        return uri.equals("/api/auth/login")
            || uri.equals("/api/auth/register");
    }
}