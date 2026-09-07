package com.bulton.api_student_management.dto.response.lookup;

import java.io.Serializable;

import com.bulton.api_student_management.entity.Province;

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
public class ProvinceResponse implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long id; 
    private String name; 
    private String countryCode; 
    public static ProvinceResponse fromEntity(
        Province province
    ){
        return ProvinceResponse.builder()
                .id(province.getId())
                .name(province.getName())
                .countryCode(province.getCountry().getCode())
                .build();

    }
}
