package com.bulton.api_student_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.entity.District;

public interface DistrictRepository extends JpaRepository<District,Long>{
    List<District> findByProvinceIdOrderByNameAsc(
        Long provinceId
    );
}
