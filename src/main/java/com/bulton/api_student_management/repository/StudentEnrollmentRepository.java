package com.bulton.api_student_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.entity.StudentEnrollment;

public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Long>{
    
}
