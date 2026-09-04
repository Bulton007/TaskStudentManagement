package com.bulton.api_student_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor  
public class AuthResponse {
    private String accessToken;
    
    @Builder .Default
    private String tokenType = "Bearer"; 

    private Long expiresIn; 
    private String username; 
    private String role;
}
