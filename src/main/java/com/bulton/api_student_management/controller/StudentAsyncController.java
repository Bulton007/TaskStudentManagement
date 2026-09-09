package com.bulton.api_student_management.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.AsyncTaskResponse;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.service.StudentAsyncService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/api/students/async")
@RequiredArgsConstructor 
public class StudentAsyncController {
    private final StudentAsyncService studentAsyncService;
    @PostMapping("/wait")
    public ResponseEntity<ApiResponse<StudentResponse>> insertAndWait(
        @Valid @RequestBody StudentRequest request
    ){
        StudentResponse student = 
            studentAsyncService.insertAndAwait(request); 
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                    HttpStatus.CREATED.value(),
                    "Student Created Successfully",
                    student)
            );
    }
    @PostMapping ("/background")
    public ResponseEntity<ApiResponse<AsyncTaskResponse>> background(
        @Valid @RequestBody StudentRequest request, 
        Principal principal
    ){
        AsyncTaskResponse task = 
            studentAsyncService.insertInBackground(
                request,
                principal.getName());   
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
            ApiResponse.success(
                HttpStatus.ACCEPTED.value(), 
                "Student Insertion Accepted for processing", 
                task));
    }

    @GetMapping ("/task/{taskId}")
    public ResponseEntity<ApiResponse<AsyncTaskResponse>> getTask(
        @PathVariable String taskId, 
        Principal principal
    ){
        AsyncTaskResponse task = 
            studentAsyncService.getTask(taskId, principal.getName());
        return ResponseEntity.ok(
            ApiResponse.success(
                HttpStatus.OK.value(),
                taskId,
                task)
        );
    }
}
