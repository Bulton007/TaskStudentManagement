package com.bulton.api_student_management.audit.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (name = "student_operations")
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class StudentOperation {
    @Id 
    @GeneratedValue (
        strategy = GenerationType.IDENTITY
    )
    private Long id; 

    @Column (
        name = "student_id"
    )
    private Long studentId; 

    @Enumerated (
        EnumType.STRING
    )
    @Column (
        nullable = false, 
        length = 20
    )
    private StudentOperationType operation; 

    @Column (
        name = "performed_by", 
        nullable = false, 
        length = 100
    )
    private String performedBy; 

    @Column (
        nullable = false, 
        length = 500
    )
    private String description;

    @Column (
        name = "performed_at",
        nullable = false, 
        updatable = false
    )
    private LocalDateTime performedAt; 

    @PrePersist 
    public void beforeInsert(){
        if(performedAt == null){
            performedAt = LocalDateTime.now();
        }
    }
}
