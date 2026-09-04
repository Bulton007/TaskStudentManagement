package com.bulton.api_student_management.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity  
@Table(
    name = "students", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_students_student_name", 
            columnNames = "student_name"
        ), 
        @UniqueConstraint(
            name = "uk_students_email", 
            columnNames = "email"
        )
    }
)
@Getter  
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class Student {
    @Id  
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; 

    @Column(name = "student_code",nullable = false, length = 50)
    private String studentCode; 

    @Column(name = "first_name", nullable=false, length=50) 
    private String firstName; 

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName; 

    @Column(name = "email",length = 100,unique = true) 
    private String email; 

    @Column(name = "phone", length = 15, unique = true)
    private String phone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; 

    @Enumerated(EnumType.STRING) 
    @Column(length = 10)
    private Gender gender; 

    @Column(length = 255)
    private String address; 

    @Column(name = "create_at", nullable = false,updatable = false)
    private LocalDateTime createdAt; 

    @Column(name = "udpated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist 
    protected void beforeInsert(){
        LocalDateTime now = LocalDateTime.now(); 
        createdAt = now; 
        updatedAt = now; 
    }

    @PreUpdate 
    protected void beforeUpdate(){
        updatedAt = LocalDateTime.now();
    }

}
