package com.bulton.api_student_management.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.StudentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class StudentAsyncWorker {
    private final StudentService studentService; 
    @Async("securedStudentTaskExecutor")
    public CompletableFuture<StudentResponse> insert(
        String taskId, 
        StudentRequest request
    ){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication == null   
            ? "anonymous"
            : authentication.getName();
        log.info(
            "ASYNC_INSERT_STARTED taskId={} user={} thread={}", 
            taskId, 
            username, 
            Thread.currentThread().getName()
        );

        try{
            StudentResponse student = studentService.createStudent(request);
            log.info(
                "ASYNC_INSERT_SUCCESS taskId={} user={} thread={}", 
                taskId,
                username, 
                student.getId()
            );
            return CompletableFuture.completedFuture(student);
        }catch(Exception exception){
            log.error(
                "ASYNC_INSERT_FAILED taskId={} user={}", 
                taskId, 
                username, 
                exception
            );
            return CompletableFuture.failedFuture(exception);
        }
    }
}
