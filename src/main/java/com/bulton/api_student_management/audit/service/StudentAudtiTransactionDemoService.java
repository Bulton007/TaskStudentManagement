package com.bulton.api_student_management.audit.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import com.bulton.api_student_management.audit.entity.StudentOperation;
import com.bulton.api_student_management.audit.entity.StudentOperationType;
import com.bulton.api_student_management.audit.repository.StudentOperationRepsoitory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Profile("tx-lab")
@RequiredArgsConstructor 
@Slf4j 
public class StudentAudtiTransactionDemoService {
    private final StudentOperationRepsoitory repsoitory; 
    @Transactional (
        transactionManager = "transaction", 
        propagation = Propagation.REQUIRED
    )
    public void insertAudit(
        Long studentId, 
        String username, 
        String studentCode, 
        boolean failBeforeCommit
    ){
        StudentOperation operation = StudentOperation.builder()
            .studentId(studentId)
            .operation(StudentOperationType.CREATE)
            .performedBy(username)
            .description("TX_LAB studentCode : " + studentCode)
            .build();
        repsoitory.saveAndFlush(operation); 
        log.info(
            "TX_LAB_AUDIT_FLUSHED studentCode={} studentId={}",
            studentCode, 
            studentId
        );

        if(failBeforeCommit){
            throw new IllegalStateException(
                "TX_LAB: simulated failure before audit commit"
            );
        }
    }
}
