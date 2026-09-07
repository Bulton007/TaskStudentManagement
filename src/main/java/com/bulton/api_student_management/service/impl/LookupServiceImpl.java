package com.bulton.api_student_management.service.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulton.api_student_management.dto.response.lookup.CountryResponse;
import com.bulton.api_student_management.dto.response.lookup.DistrictResponse;
import com.bulton.api_student_management.dto.response.lookup.ProvinceResponse;
import com.bulton.api_student_management.entity.District;
import com.bulton.api_student_management.exception.ResourceNotFoundException;
import com.bulton.api_student_management.repository.CountryRepository;
import com.bulton.api_student_management.repository.DistrictRepository;
import com.bulton.api_student_management.repository.ProvinceRepository;
import com.bulton.api_student_management.service.LookupService;
import com.bulton.api_student_management.service.RedisService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional(readOnly = true)
public class LookupServiceImpl implements LookupService{
    private static final String COUNTRY_CACHE_KEY = "lookup:countires"; 
    private static final String PROVINCE_CAHCE_KEY = "lookup:provinces"; 
    private static final String DISTRICT_CACHE_KEY = "lookup:districts"; 
    private static final Duration LOOK_UP_TTL = Duration.ofHours(24); 
    private final CountryRepository countryRepository; 
    private final ProvinceRepository provinceRepository; 
    private final DistrictRepository districtRepository; 
    private final RedisService redisService;

    @Override
    public List<CountryResponse> getCountries() {
        Optional<CountryResponse[]> cache = 
            redisService.get(
                COUNTRY_CACHE_KEY, 
                CountryResponse[].class);
        if(cache.isPresent()){
            return Arrays.asList(cache.get()); 
        }
        List<CountryResponse> countries = 
            countryRepository
                .findAllByOrderByNameAsc()
                .stream()
                .map(CountryResponse::fromEntity)
                .toList();
        return countries;
    }

    @Override
    public List<ProvinceResponse> getProvince(String countryCode) {
        String normalizedCode = 
            countryCode.trim().toUpperCase(); 
        
        countryRepository   
            .findByCodeIgnoreCase(normalizedCode)
            .orElseThrow(() -> 
                new ResourceNotFoundException(
                    "Country Not Found with Code : " + normalizedCode
                )
        );
        String cacheKey = PROVINCE_CAHCE_KEY + normalizedCode;
        Optional<ProvinceResponse[]> cached = 
            redisService.get(
                cacheKey, 
                ProvinceResponse[].class); 
            if( cached.isPresent()){
                return Arrays.asList(cached.get()); 
            }
        List<ProvinceResponse> provinces = 
            provinceRepository
                .findByCountryCodeIgnoreCaseOrderByNameAsc(normalizedCode)
                .stream()
                .map(ProvinceResponse::fromEntity)
                .toList();
        redisService.set(
            cacheKey,
            provinces,
            LOOK_UP_TTL);
        return provinces;           
    }

    @Override
    public List<DistrictResponse> getDistrict(Long provinceId) {
        provinceRepository
            .findById(provinceId)
            .orElseThrow(() -> 
            new ResourceNotFoundException(
                "Province not Found With ID : " + provinceId
            )
        );
        String cacheKey = DISTRICT_CACHE_KEY + provinceId; 
        Optional<DistrictResponse[]> cached = redisService.get(
            cacheKey,
            DistrictResponse[].class
        );
        if(cached.isPresent()){
            return Arrays.asList(cached.get());
        }

        List<DistrictResponse> districts = 
            districtRepository
                .findByProvinceIdOrderByNameAsc(provinceId)
                .stream()
                .map(DistrictResponse::fromEntity)
                .toList();
        redisService.set(
            cacheKey,
            districts,
            LOOK_UP_TTL);
        return districts;
    }
}
