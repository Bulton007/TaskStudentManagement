package com.bulton.api_student_management.audit.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.bulton.api_student_management.audit.entity.StudentOperation;
import com.bulton.api_student_management.audit.entity.StudentOperationType;
import com.bulton.api_student_management.audit.repository.StudentOperationRepsoitory;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class StudentAuditService {
    private final StudentOperationRepsoitory 
        studentOperationRepsoitory;

    @Transactional (
        transactionManager = "transactionManager", 
        propagation = Propagation.REQUIRED
    )
    public void recordOperation(
        Long stduentId, 
        StudentOperationType operation, 
        String performedBy, 
        String description
    ){
        StudentOperation studentOperation = 
        StudentOperation.builder()
            .studentId(stduentId)
            .operation(operation)
            .performedBy(performedBy)
            .description(description)
            .build(); 
        studentOperationRepsoitory.saveAndFlush(studentOperation);
    }

}
