package com.bulton.api_student_management.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class RegisterRequest {
    @NotBlank (message = "Username is required")
    @Size (min = 5, max = 50)
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$", 
        message = "Usernanme can contain only letter, number and underscore"
    )
    private String username; 

    @NotBlank(message = "Email is required")
    @Email (message= "email is invalid")
    private String email; 

    @NotBlank(message = "Password is required") 
    @Size(min = 12, max = 100, message = "Password must be contain from 12 to 100 characters")
    private String password; 


    
}
