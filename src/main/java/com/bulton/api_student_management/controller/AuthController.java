package com.bulton.api_student_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.dto.request.LoginRequest;
import com.bulton.api_student_management.dto.request.RegisterRequest;
import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.AuthResponse;
import com.bulton.api_student_management.dto.response.UserResponse;
import com.bulton.api_student_management.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/auth")
@RequiredArgsConstructor 
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
        @Valid @RequestBody  RegisterRequest request
    ){  
        UserResponse user = authService.register(request);
        ApiResponse<UserResponse> response = ApiResponse.success(
            HttpStatus.CREATED.value(),
            "User Created Successfully", user);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
        @Valid @RequestBody LoginRequest request
    ){
        AuthResponse authentication = authService.login(request); 
        ApiResponse<AuthResponse> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Login Successed", 
            authentication);
        return ResponseEntity.ok(response);
    }
}
