package com.bulton.api_student_management.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.lookup.CountryResponse;
import com.bulton.api_student_management.dto.response.lookup.DistrictResponse;
import com.bulton.api_student_management.dto.response.lookup.ProvinceResponse;
import com.bulton.api_student_management.service.LookupService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequestMapping("api/lookups")
@RequiredArgsConstructor 
public class LookupController {
    private final LookupService lookupService; 
    @GetMapping("/countries")
    public ResponseEntity<
        ApiResponse<List<CountryResponse>>
    > getCountries(){
        List<CountryResponse> countries = lookupService.getCountries();
        ApiResponse<List<CountryResponse>> response = 
            ApiResponse.success(
            HttpStatus.OK.value(),
            "Country Recieved Successfully",
            countries);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/countries/{countryCode}/provinces")
    public ResponseEntity<
        ApiResponse<List<ProvinceResponse>>
    > getProvinces(
        @PathVariable  String countryCode
    ){
        List<ProvinceResponse> provinces = lookupService.getProvince(countryCode); 
        ApiResponse<List<ProvinceResponse>> response = 
            ApiResponse.success(
            HttpStatus.OK.value(),
            "District was Successfully",
            provinces);
        return ResponseEntity.ok(response); 
    }

    @GetMapping("/provinces/{provinceId}/districts")
    public ResponseEntity<
        ApiResponse<List<DistrictResponse>>
    > getDistricts(
        @PathVariable Long provinceId
    ){
        List<DistrictResponse> districrs = lookupService.getDistrict(provinceId); 
        ApiResponse<List<DistrictResponse>> response = 
        ApiResponse.success(
        HttpStatus.OK.value(),
        "District Recieved Succcessfully",
        districrs); 
        return ResponseEntity.ok(response);
    }
    
    
}
