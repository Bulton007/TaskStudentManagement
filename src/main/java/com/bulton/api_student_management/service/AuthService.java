package com.bulton.api_student_management.service;

import com.bulton.api_student_management.dto.request.LoginRequest;
import com.bulton.api_student_management.dto.request.RegisterRequest;
import com.bulton.api_student_management.dto.response.AuthResponse;
import com.bulton.api_student_management.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(RegisterRequest request); 
    AuthResponse login(LoginRequest request); 
}
