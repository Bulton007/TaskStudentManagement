package com.bulton.api_student_management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record StudentEnrollmentRequest (
    @NotNull @Valid StudentRequest student, 

    @NotBlank 
    @Size (max = 50)
    String enrollmentReference
){

}
