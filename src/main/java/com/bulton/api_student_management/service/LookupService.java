package com.bulton.api_student_management.service;

import java.util.List;

import com.bulton.api_student_management.dto.response.lookup.CountryResponse;
import com.bulton.api_student_management.dto.response.lookup.DistrictResponse;
import com.bulton.api_student_management.dto.response.lookup.ProvinceResponse;

public interface LookupService {
    List<CountryResponse> getCountries();
    List<ProvinceResponse> getProvince(
        String countryCode
    ); 
    List<DistrictResponse> getDistrict(
        Long provinceId
    );
}
