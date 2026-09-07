package com.bulton.api_student_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(
    name = "countries", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_countries_code", 
            columnNames = "code"
        )
    }
)
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class Country {
    @Id 
    @GeneratedValue(
        strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(
        nullable = false, 
        length = 3
    )
    private String code; 

    @Column(
        nullable = false, 
        length = 100
    )
    private String name;
}
