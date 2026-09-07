package com.bulton.api_student_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table (
    name = "districts", 
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_district_province_name", 
            columnNames = {
                "province_id", 
                "name"
            }
        )
    }
)
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class District {
    
    @Id 
    @GeneratedValue( strategy = GenerationType.IDENTITY) 
    private Long id; 

    @Column (
        nullable = false, 
        length = 100
    )
    private String name; 

    @ManyToOne(
        fetch = FetchType.LAZY, 
        optional = false
    )
    @JoinColumn (
        name = "province_id", 
        nullable = false
    )
    private Province province;

}
