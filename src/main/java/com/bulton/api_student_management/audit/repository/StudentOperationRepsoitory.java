package com.bulton.api_student_management.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.audit.entity.StudentOperation;

public interface StudentOperationRepsoitory extends JpaRepository<StudentOperation, Long>{
    List<StudentOperation> findByStudentIdOrderByPerformedAtDesc(
        Long studentId
    );
    
    List<StudentOperation> findAllByOrderByPerformedAtDesc();
}
