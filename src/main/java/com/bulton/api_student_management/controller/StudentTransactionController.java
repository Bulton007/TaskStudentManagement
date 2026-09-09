package com.bulton.api_student_management.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.dto.request.StudentEnrollmentRequest;
import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.service.StudentTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("api/students/transactions")
@RequiredArgsConstructor 
public class StudentTransactionController {
    private final StudentTransactionService transactionService;
    @PostMapping ("/same-database")
    public ResponseEntity<ApiResponse<StudentResponse>> create(
        @Valid @RequestBody StudentEnrollmentRequest request
    ){
        StudentResponse student = 
            transactionService.createStudentAndEnrollment(request); 
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Student and Enrollment updated successfully",
                student)
        );
    }
}
