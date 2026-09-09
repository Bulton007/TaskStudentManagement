package com.bulton.api_student_management.dto.response;

import java.time.Instant;

public record AsyncTaskResponse (
    String taskId, 
    String status, 
    StudentResponse result, 
    String error, 
    Instant submittedAt, 
    Instant completedAt
){

}
