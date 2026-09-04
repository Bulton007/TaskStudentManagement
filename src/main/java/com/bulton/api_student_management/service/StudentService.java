package com.bulton.api_student_management.service;

import java.util.List;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.StudentResponse;

public interface StudentService {
    StudentResponse createStudent( StudentRequest request); 
    List<StudentResponse> getAllStudents();
    StudentResponse getStudentById(Long id); 
    StudentResponse updateStudent(Long id, StudentRequest request); 
    void deleteStudent(Long id);
}
