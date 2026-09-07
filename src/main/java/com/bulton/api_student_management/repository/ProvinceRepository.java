package com.bulton.api_student_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.entity.Province;
import java.util.List;


public interface ProvinceRepository extends JpaRepository<Province, Long>{
    List<Province> findByCountryCodeIgnoreCaseOrderByNameAsc(String countryCode);
}
