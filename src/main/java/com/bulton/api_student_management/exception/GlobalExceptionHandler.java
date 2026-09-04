package com.bulton.api_student_management.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bulton.api_student_management.dto.response.ApiResponse;

@RestControllerAdvice 
@Slf4j 
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
        ResourceNotFoundException exception
    ){
        log.warn("Resource Not Found: {}",exception.getMessage());
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.NOT_FOUND.value(),
            exception.getMessage(),
            null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(DuplicationResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(
        DuplicationResourceException exception
    ){
        log.warn("Duplication Resource : {}",exception.getMessage());
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.CONFLICT.value(), 
            exception.getMessage(), 
            null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestValidation(
        MethodArgumentNotValidException exception
    ){
        Map<String, String> errors = new HashMap<>(); 
        exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                errors.put(error.getField(),error.getDefaultMessage())
            );
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.BAD_REQUEST.value(), 
            "Parameters validation Failed", errors);
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
        ConstraintViolationException exception
    ){
        Map<String, String> errors = new HashMap<>(); 
        exception.getConstraintViolations().forEach(violation ->
            errors.put(
                violation.getPropertyPath().toString(),
                violation.getMessage()
            )
        );
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.BAD_REQUEST.value(), 
            "Parameters validated Failed", errors);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(
        Exception exception
    ){
        log.error("Unexpected server error", exception);
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
             "An Unexpected Server Error", null);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(
        BadCredentialsException exception
    ){
        ApiResponse<Void> response = ApiResponse.failure(
            HttpStatus.UNAUTHORIZED.value(), 
            "Invalid Username or Password", null); 
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(response);
    }
    @ExceptionHandler(ThirdPartyApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleThirdPartyApi(
    ThirdPartyApiException exception
    ) {
    log.error(
        "Third-party API failure: {}",
        exception.getMessage(),
        exception
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
    
}
