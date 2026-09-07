package com.bulton.api_student_management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bulton.api_student_management.entity.Country;

public interface CountryRepository  extends JpaRepository<Country, Long>{
    Optional<Country> findByCodeIgnoreCase(
        String code
    ); 
    List<Country> findAllByOrderByNameAsc(); 
    boolean existsByCodeIgnoreCase(
        String code
    );
}
