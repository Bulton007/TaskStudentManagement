package com.bulton.api_student_management.dto.response.lookup;

import java.io.Serializable;

import com.bulton.api_student_management.entity.District;

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
public class DistrictResponse implements Serializable{
    private static final long serialVersionUID = 1L; 
    private Long id;
    private String name; 
    private Long provinceId; 
    public static DistrictResponse fromEntity(
        District district
    ){
        return DistrictResponse.builder()
            .id(district.getId())
            .name(district.getName())
            .provinceId(district.getProvince().getId())
            .build();
    }
}
