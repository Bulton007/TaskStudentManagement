package com.bulton.api_student_management.dto.response.lookup;

import java.io.Serializable;

import com.bulton.api_student_management.entity.Country;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class CountryResponse implements Serializable {
    private static final long serialVersionUID = 1L; 
    private Long id; 
    private String code; 
    private String name; 
    public static  CountryResponse fromEntity(
        Country country
    ){
        return CountryResponse.builder()
            .id(country.getId())
            .code(country.getCode())
            .name(country.getName())
            .build();
    }
}
