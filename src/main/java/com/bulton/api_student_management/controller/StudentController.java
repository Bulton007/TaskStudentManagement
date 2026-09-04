package com.bulton.api_student_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.service.StudentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping("/api/students")
@RequiredArgsConstructor 
@Validated 
public class StudentController {
    private final StudentService studentService;

    //Create Student
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
        @Valid @RequestBody StudentRequest request
    ){
        StudentResponse student = studentService.createStudent(request);
        ApiResponse<StudentResponse> response = ApiResponse.success(
            HttpStatus.CREATED.value(), 
            "Student create sucessfully", student);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Get All Students 
    @GetMapping 
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents(){
        List<StudentResponse> students = studentService.getAllStudents();
        ApiResponse<List<StudentResponse>> response = ApiResponse.success(
            HttpStatus.OK.value(),
            "Students recived Successfully", students);
        return ResponseEntity.status(HttpStatus.OK).body(response); 
    }

    //Get Student By Id 
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
        @PathVariable @Positive( message = "ID Must be Greater Than 0") Long id
    ){
        StudentResponse students = studentService.getStudentById(id); 
        ApiResponse<StudentResponse> response = ApiResponse.success(
            HttpStatus.OK.value(), 
            "Student recieved Successfully", students);
        return ResponseEntity.ok(response);
    }

    //Update Student 
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
        @PathVariable @Positive (message = "ID must be greater than 0") Long id,
        @Valid @RequestBody StudentRequest request
    ){
        StudentResponse student = studentService.updateStudent(id, request);
        ApiResponse<StudentResponse> response = ApiResponse.success(
            HttpStatus.OK.value(), 
            "Update Successfully", student);
        return ResponseEntity.ok(response);
    }

    //Delete Student 
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
        @PathVariable @Positive (message = "ID must not be greater than 0") Long id
    ){
        studentService.deleteStudent(id);
        ApiResponse<Void> response = ApiResponse.success(
            HttpStatus.OK.value(), "Deleted Successfully", null); 
        return ResponseEntity.ok(response);
    }




    
    
}
