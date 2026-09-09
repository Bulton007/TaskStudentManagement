package com.bulton.api_student_management.controller;

import java.security.Principal;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.audit.service.StudentTwoDatabaseDemoService;
import com.bulton.api_student_management.audit.service.StudentTwoDatabaseDemoService.FaluireMode;
import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.StudentResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@Profile ("tx-lab")
@RequestMapping ("/api/students/transactions/two-database")
@RequiredArgsConstructor 
public class StudentAuditDemoController {
    private final StudentTwoDatabaseDemoService service; 
    @PostMapping 
    public ResponseEntity<ApiResponse<StudentResponse>> create(
        @Valid @RequestBody StudentRequest request, 
        @RequestParam (defaultValue = "NONE") FaluireMode faluire, 
        Principal principal
    ){
        StudentResponse student = service.create(
            request,
            principal.getName(),
            faluire);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(
                HttpStatus.CREATED.value(),
                "Student and Audit are commited",
                student)
        );
    }
}
