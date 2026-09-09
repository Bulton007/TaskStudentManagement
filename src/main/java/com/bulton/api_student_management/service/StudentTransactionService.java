package com.bulton.api_student_management.service;

import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.bulton.api_student_management.repository.StudentRepository;
import com.bulton.api_student_management.dto.request.StudentEnrollmentRequest;
import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.entity.Student;
import com.bulton.api_student_management.entity.StudentEnrollment;
import com.bulton.api_student_management.exception.DuplicationResourceException;
import com.bulton.api_student_management.repository.StudentEnrollmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@RequiredArgsConstructor 
@Slf4j 
public class StudentTransactionService {
    private final StudentRepository studentRepository; 
    private final StudentEnrollmentRepository enrollmentRepository;
    private final RedisService redisService; 

    @Transactional (transactionManager = "transactionManager")
    public StudentResponse createStudentAndEnrollment(
        StudentEnrollmentRequest request
    ){
        StudentRequest input = request.student();
        String studentCode = input.getStudentCode().trim(); 
        String email =  input.getEmail()
            .trim()
            .toLowerCase(Locale.ROOT);
        if(studentRepository.existsByStudentCode(studentCode)){
            throw new DuplicationResourceException(
                "Student Email already exists: " + email
            );
        }

        Student student = Student.builder()
            .studentCode(studentCode)
            .firstName(input.getFirstName().trim())
            .lastName(input.getLastName().trim())
            .email(email)
            .phone(input.getPhone())
            .dateOfBirth(input.getDateOfBirth())
            .gender(input.getGender())
            .address(input.getAddress())
            .build();
        //Insert 1 SQL Executes but not commit yets
        Student saved = studentRepository.save(student);

        log.info(
            "TX_FIRST_INSERTED_FLUSHED studentId={} studentCode={}", 
            saved.getId(), 
            studentCode
        ); 
        
        StudentEnrollment enrollment = new StudentEnrollment(); 
        enrollment.setStudentId(saved.getId());
        enrollment.setReferenceCode(
            request.enrollmentReference().trim()
        );
        //Insert 2 : duplicate reference cause a persistence exception 
        enrollmentRepository.saveAndFlush(enrollment); 
        log.info(
            "TX_SECOND_INSERT_FLUSHED studentId={} reference={}",
            saved.getId(), 
            enrollment.getReferenceCode() 
        );

        //Invalidate 
        TransactionSynchronizationManager.registerSynchronization(
            new TransactionSynchronization() {
                @Override 
                public void afterCommit() {
                    log.info(
                        "TX_COMMITED studentId={} reference={}", 
                        saved.getId(), 
                        enrollment.getReferenceCode()
                    );
                    try{
                        redisService.delete("students:all");
                    } catch(Exception exception){
                        //This is rollback
                        log.error(
                            "TX_CACHE_INVALIDATION_FALIED studentId={}", 
                            saved.getId(), 
                            exception
                        );
                    }
                };
            }
        );
        return StudentResponse.fromEntity(saved);

    }

}
