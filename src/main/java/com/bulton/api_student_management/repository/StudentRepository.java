package com.bulton.api_student_management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.entity.Student;


public interface StudentRepository extends JpaRepository<Student,Long>{
    Optional<Student> findByStudentCode(String studentCode); 
    Optional<Student> findByEmail(String email); 
    boolean existsByStudentCode(String studentCode); 
    boolean existsByEmail(String email);
}
