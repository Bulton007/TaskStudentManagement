package com.bulton.api_student_management.dto.request;

import java.time.LocalDate;

import com.bulton.api_student_management.entity.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class StudentRequest {
    @NotBlank(message = "StudentCode is required")
    @Size(min = 3, max = 20, message = "Student Code must contain 3 to 20 characters")
    private String studentCode; 

    @NotBlank(message = "FirstName is Required") 
    @Size(min = 3, max= 50, message = "firstname must contain 3 to 50 characters") 
    private String firstName; 

    @NotBlank(message = "lastname is required") 
    @Size(min = 3, max = 50, message = "lastname must contain 3 to 50 characters")
    private String lastName;

    @NotBlank(message = "email is required") 
    @Email(message = "Email format is invalid")
    private String email;

    @Pattern(
        regexp = "^[0-9]{8,15}$", 
        message = "Phone Must Contain from 8 to 15"
    )
    private String phone;

    @NotNull(message = "date of birth is required")
    @Past(message = "date of birth must be in the past")
    private LocalDate dateOfBirth; 

    @NotNull(message = "gender is required")
    private Gender gender;

    @Size(max = 255, message = "Address cannot be exceed 255 characters") 
    private String address;
}
