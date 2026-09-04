package com.bulton.api_student_management.entity;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(
    name = "users", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_users_username", 
            columnNames = "username"
        ), 
        @UniqueConstraint(
            name="uk_users_email", 
            columnNames = "email"
        )
    }
)
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class User {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; 

    @Column(name = "username",length = 50)
    private String username; 

    @Column(name = "email",length = 100,unique = true)
    private String email;

    @Column(nullable = false) 
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    @Builder.Default
    @Column(nullable = false) 
    private boolean enabled = true;

    @Column(name = "created_at", updatable = false,nullable = false) 
    private LocalDateTime createdAt; 

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; 

    @PrePersist 
    protected void beforeInsert(){
        LocalDateTime now = LocalDateTime.now(); 
        if(role == null){
            role = Role.USER;
        }
        createdAt = now; 
        updatedAt = now;
    }

    @PreUpdate 
    protected void beforeUpdate(){
        updatedAt = LocalDateTime.now();
    }
    
}
