package com.bulton.api_student_management.service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.AsyncTaskResponse;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class StudentAsyncService {
    private final StudentAsyncWorker worker; 
    private final ConcurrentHashMap<String, OwnedTask> tasks = 
        new ConcurrentHashMap<>();
    private record OwnedTask(
        String owner, 
        AsyncTaskResponse response
    ){

    }
    public StudentResponse insertAndAwait(StudentRequest request){
        String taskId = UUID.randomUUID().toString(); 
        try{
            return submit(taskId,request).join();
        }catch(CompletionException exception){
            Throwable cause = unwrap(exception); 
            if(cause instanceof RuntimeException runtimeException){
                throw runtimeException;
            }
            throw new IllegalStateException(
                "Asynchronous student insertion failed", 
                cause
            );
        }
    }
    public AsyncTaskResponse insertInBackground(
        StudentRequest request, 
        String owner
    ){
        removeExpiredTasks(); 
        String taskId = UUID.randomUUID().toString();
        Instant submittedAt = Instant.now();

        AsyncTaskResponse initial = new AsyncTaskResponse(
            taskId, 
            "PENDING", 
            null, 
            null, 
            submittedAt, 
            null
        ); 

        tasks.put(taskId,new OwnedTask(owner, initial)); 

        final CompletableFuture<StudentResponse> future; 

        try{
            future = submit(taskId,request); 
        } catch(RuntimeException exception){
            tasks.remove(taskId); 
            throw exception;
        }

        future.whenComplete((student ,error) ->{
            AsyncTaskResponse completed; 
            if(error == null){
                completed = new AsyncTaskResponse(
                    taskId, 
                    "SUCCESS", 
                    student, 
                    null,
                    submittedAt,
                    Instant.now()
                );
            }else{
                completed = new AsyncTaskResponse(
                    taskId,
                    "FAILED", 
                    null,
                    "Student Insertion Failed. Reference: " + taskId, 
                    submittedAt, 
                    Instant.now()
                );
            }

            tasks.put(taskId, new OwnedTask(owner, completed));
        });
        return initial;

    }

    public AsyncTaskResponse getTask(String taskId, String owner){
        removeExpiredTasks(); 
        OwnedTask task = tasks.get(taskId); 
        if(task == null || !task.owner().equals(owner)){
            throw new ResourceNotFoundException(
                "Task Not Found : " + taskId
            );
        }
        return task.response();
    }
    private CompletableFuture<StudentResponse> submit(
        String taskId, 
        StudentRequest request
    ){
        try{
            return worker.insert(taskId,request);
        }catch(RejectedExecutionException exception){
            log.error(
                "ASYNC_INSERT_REJECTED taskId={}", 
                taskId, 
                exception
            );
            throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE, 
                "Background executor is busy. Try again later", 
                exception
            ); 

        }
    }

    private Throwable unwrap(Throwable error){
        while (
            error instanceof CompletionException
                && error.getCause() != null
        ) {
            error = error.getCause();
        }
        return error;
    }
    private void removeExpiredTasks(){
        Instant cutoff = Instant.now().minus(Duration.ofHours(1)); 
        tasks.entrySet().removeIf(
            entry -> {
                Instant completedAt = 
                    entry.getValue().response().completedAt();
                return completedAt != null
                    && completedAt.isBefore(cutoff);
            }
        );
    }
}
