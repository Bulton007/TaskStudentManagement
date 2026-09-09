package com.bulton.api_student_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(
    name = "student_enrollments", 
    uniqueConstraints = @UniqueConstraint (
        name = "uk_student_enrollment_reference", 
        columnNames = "reference_code"
    )
) 
@Getter 
@Setter 
@NoArgsConstructor 
public class StudentEnrollment {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "student_id", nullable = false)
    private Long studentId; 

    @Column (name = "reference_code",nullable = false, length = 50)
    private String referenceCode;

}
