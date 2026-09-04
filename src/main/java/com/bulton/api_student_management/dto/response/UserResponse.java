package com.bulton.api_student_management.dto.response;

import java.time.LocalDateTime;
import com.bulton.api_student_management.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter 
@Builder  
public class UserResponse {
    private Long id;
    private String username; 
    private String email; 
    private String role; 
    private boolean enabled; 
    private LocalDateTime createdAt; 
    public static UserResponse fromEntity(User user){
        return UserResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().name())
            .enabled(user.isEnabled())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
