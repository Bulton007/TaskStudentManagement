package com.bulton.api_student_management.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bulton.api_student_management.dto.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private static final Logger ERROR_LOG =
        LoggerFactory.getLogger("API_ERROR");

    private final ObjectMapper objectMapper;

    /*
     * 404 - Resource not found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
        ResourceNotFoundException exception,
        HttpServletRequest request
    ) {
        writeErrorLog(
            request,
            HttpStatus.NOT_FOUND,
            exception,
            null,
            false
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            null
        );

        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(response);
    }

    /*
     * 409 - Duplicate resource
     */
    @ExceptionHandler(DuplicationResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(
        DuplicationResourceException exception,
        HttpServletRequest request
    ) {
        writeErrorLog(
            request,
            HttpStatus.CONFLICT,
            exception,
            null,
            false
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.CONFLICT.value(),
            exception.getMessage(),
            null
        );

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(response);
    }

    /*
     * 400 - Request body validation failure
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestValidation(
        MethodArgumentNotValidException exception,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.put(
                    error.getField(),
                    error.getDefaultMessage()
                )
            );

        writeErrorLog(
            request,
            HttpStatus.BAD_REQUEST,
            exception,
            errors,
            false
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.BAD_REQUEST.value(),
            "Parameter validation failed",
            errors
        );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    /*
     * 400 - Path variable or request parameter validation failure
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
        ConstraintViolationException exception,
        HttpServletRequest request
    ) {
        Map<String, String> errors = new HashMap<>();

        exception.getConstraintViolations()
            .forEach(violation ->
                errors.put(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
                )
            );

        writeErrorLog(
            request,
            HttpStatus.BAD_REQUEST,
            exception,
            errors,
            false
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.BAD_REQUEST.value(),
            "Parameter validation failed",
            errors
        );

        return ResponseEntity
            .badRequest()
            .body(response);
    }

    /*
     * 401 - Invalid login credentials
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
        BadCredentialsException exception,
        HttpServletRequest request
    ) {
        writeErrorLog(
            request,
            HttpStatus.UNAUTHORIZED,
            exception,
            null,
            false
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.UNAUTHORIZED.value(),
            "Invalid username or password",
            null
        );

        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }

    /*
     * 502 - Third-party API failure
     */
    @ExceptionHandler(ThirdPartyApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleThirdPartyApi(
        ThirdPartyApiException exception,
        HttpServletRequest request
    ) {
        writeErrorLog(
            request,
            HttpStatus.BAD_GATEWAY,
            exception,
            null,
            true
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.BAD_GATEWAY.value(),
            exception.getMessage(),
            null
        );

        return ResponseEntity
            .status(HttpStatus.BAD_GATEWAY)
            .body(response);
    }

    /*
     * 500 - Unexpected application error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
        Exception exception,
        HttpServletRequest request
    ) {
        writeErrorLog(
            request,
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception,
            null,
            true
        );

        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected server error occurred",
            null
        );

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }

    /*
     * Creates one JSON error-log entry.
     */
    private void writeErrorLog(
        HttpServletRequest request,
        HttpStatus status,
        Exception exception,
        Map<String, String> validationErrors,
        boolean includeStackTrace
    ) {
        try {
            ObjectNode logEntry = objectMapper.createObjectNode();

            logEntry.put("event", "API_ERROR");
            logEntry.put("timestamp", LocalDateTime.now().toString());
            logEntry.put("method", request.getMethod());
            logEntry.put("uri", createRequestUri(request));
            logEntry.put("status", status.value());
            logEntry.put("user", getUsername(request));
            logEntry.put(
                "exception",
                exception.getClass().getSimpleName()
            );
            logEntry.put(
                "message",
                safeMessage(exception)
            );

            if (validationErrors != null &&
                !validationErrors.isEmpty()) {

                logEntry.set(
                    "validationErrors",
                    objectMapper.valueToTree(validationErrors)
                );
            }

            String jsonLog =
                objectMapper.writeValueAsString(logEntry);

            if (includeStackTrace) {
                ERROR_LOG.error(jsonLog, exception);
            } else {
                ERROR_LOG.error(jsonLog);
            }

        } catch (Exception loggingException) {
            ERROR_LOG.error(
                "Failed to create JSON error log: {}",
                exception.getMessage(),
                loggingException
            );
        }
    }

    private String getUsername(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "anonymous";
        }

        return request.getUserPrincipal().getName();
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

    private String safeMessage(Exception exception) {
        if (exception.getMessage() == null ||
            exception.getMessage().isBlank()) {

            return "No error message available";
        }

        return exception.getMessage();
    }
}