package com.bulton.api_student_management.audit.service;

import java.util.Locale;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.entity.Student;
import com.bulton.api_student_management.exception.DuplicationResourceException;
import com.bulton.api_student_management.repository.StudentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Profile ("tx-lab")
@RequiredArgsConstructor 
@Slf4j 

public class StudentTwoDatabaseDemoService {
    public enum FaluireMode{
        NONE, 
        AUDIT_BEFORE_COMMIT,
        PRIMARY_AFTER_AUDIT_COMMIT
    }

    private final StudentRepository studentRepository; 
    private final StudentAudtiTransactionDemoService auditDemoService;
    @Transactional (transactionManager = "transactionManager")
    public StudentResponse create(
        StudentRequest request, 
        String username, 
        FaluireMode faluire
    ){
        String studentCode = request.getStudentCode().trim(); 
        String email = request.getEmail()
            .trim()
            .toLowerCase(Locale.ROOT);
        if( studentRepository.existsByEmail(email)){
            throw new DuplicationResourceException(
                "Student Email already exits :" + email
            );
        }

        Student student = Student.builder()
            .studentCode(studentCode)
            .firstName(request.getFirstName().trim())
            .lastName(request.getLastName().trim())
            .email(email)
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .build();
        
        Student saved = studentRepository.save(student); 

        log.info(
            "TX_LAB_PRIMARY_FLUSHED studentCode={} studentId={}", 
            studentCode,
            saved.getId()
        );

        auditDemoService.insertAudit(
            saved.getId(),
            username,
            studentCode,
            faluire == FaluireMode.AUDIT_BEFORE_COMMIT);

        log.info(
            "TX_LAB_AUDIT_COMMITED studentCode={} studentId={}", 
            studentCode, 
            saved.getId()
        );

        if (faluire == FaluireMode.PRIMARY_AFTER_AUDIT_COMMIT) {    
            throw new IllegalStateException(
                "TX_LAB: simulated primary feature after audit commit"
            );
        }
        return StudentResponse.fromEntity(saved);
    }
}
