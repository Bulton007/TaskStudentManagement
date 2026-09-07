package com.bulton.api_student_management.dto.response;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.bulton.api_student_management.entity.Gender;
import com.bulton.api_student_management.entity.Student;

import lombok.Builder;
import lombok.Getter;

@Getter 
@Builder 
public class StudentResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id; 
    private String firstName; 
    private String lastName; 
    private String email; 
    private String phone; 
    private LocalDate dateOfBirth; 
    private Gender gender;
    private String address; 
    private LocalDateTime createdAt; 
    private LocalDateTime updatedAt;

    public static StudentResponse fromEntity(Student student){
        return StudentResponse.builder()
            .id(student.getId())
            .firstName(student.getFirstName())
            .lastName(student.getLastName())
            .email(student.getEmail())
            .phone(student.getPhone())
            .dateOfBirth(student.getDateOfBirth())
            .gender(student.getGender())
            .address(student.getAddress())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }

}
