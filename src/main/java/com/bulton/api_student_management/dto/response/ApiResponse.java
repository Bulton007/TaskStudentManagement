package com.bulton.api_student_management.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter 
@Builder 
@AllArgsConstructor 
public class ApiResponse<T> {
    private boolean success;
    private int status; 
    private String message;
    private T data; 
    private Map<String, String> error;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(
        int status, 
        String message, 
        T data
    ){
        return ApiResponse.<T>builder()
            .success(true)
            .status(status)
            .message(message)
            .data(data)
            .error(null)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ApiResponse<Void> failure(
        int status, 
        String message, 
        Map<String, String> errors
    ){
        return ApiResponse.<Void>builder()
            .success(false)
            .status(status)
            .message(message)
            .data(null)
            .error(null)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
