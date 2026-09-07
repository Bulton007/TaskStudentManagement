package com.bulton.api_student_management.service.impl;

import java.time.Duration;
import java.util.DuplicateFormatFlagsException;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulton.api_student_management.dto.request.StudentRequest;
import com.bulton.api_student_management.dto.response.StudentResponse;
import com.bulton.api_student_management.entity.Student;
import com.bulton.api_student_management.exception.DuplicationResourceException;
import com.bulton.api_student_management.exception.ResourceNotFoundException;
import com.bulton.api_student_management.repository.StudentRepository;
import com.bulton.api_student_management.service.RedisService;
import com.bulton.api_student_management.service.StudentService;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
@Transactional 
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;
    private final RedisService redisService;
    private static final String STUDENT_KEY_PREFIX = "students:"; 
    private static final String ALL_STUDENTS_KEY = "students:all"; 
    private static final Duration STUDENT_CACHE_TTL = Duration.ofMinutes(10); 
    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if(studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new DuplicateFormatFlagsException(
                "Student Code Already Exits" + request.getStudentCode()
            ); 
        }

        if(studentRepository.existsByEmail(request.getEmail())){
            throw new DuplicateFormatFlagsException(
                "Student Email Already Exits" + request.getEmail()
            );
        }
        Student student = Student.builder()
            .studentCode(request.getStudentCode().trim())
            .firstName(request.getFirstName().trim())
            .lastName(request.getLastName().trim())
            .email(request.getEmail().toLowerCase())
            .phone(request.getPhone())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .address(request.getAddress())
            .build();
        Student savedStudent = studentRepository.save(student);
        StudentResponse response = StudentResponse.fromEntity(savedStudent); 
        redisService.set(
            STUDENT_KEY_PREFIX + savedStudent.getId(), 
            response, 
            STUDENT_CACHE_TTL);
        redisService.delete(ALL_STUDENTS_KEY);
        return response;    
    }
    @Override
    @Transactional(readOnly = true)
    public StudentResponse getStudentById(Long id) {
        String cacheKey = STUDENT_KEY_PREFIX + id; 
        Optional<StudentResponse> cachedStudent = 
            redisService.get(
                cacheKey,
                StudentResponse.class);
        if( cachedStudent.isPresent()){
            return cachedStudent.get();
        } 
        Student student = findStudent(id);
        StudentResponse response = StudentResponse.fromEntity(student); 
        redisService.set(cacheKey, response, Duration.ofMinutes(10));
        return response;
    }
    @Override
    @Cacheable(value = "studentList", key = "'all'")
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll()
        .stream()
        .map(StudentResponse::fromEntity)
        .toList();
    }

    @Override
    @CachePut(value = "students", key = "#id")
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = findStudent(id); 
        boolean studentCodeChange = !student.getStudentCode().equalsIgnoreCase(request.getStudentCode());
        if( studentCodeChange && studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new DuplicationResourceException(
                "Student Code is Already Exits : " + request.getStudentCode()
            );
        }

        boolean emailChanged = !student.getEmail().equalsIgnoreCase(request.getEmail()); 

        if( emailChanged && studentRepository.existsByEmail(request.getEmail())){
            throw new DuplicationResourceException(
                "Email is Already Exits : " + request.getEmail()
            );
        }
        
        student.setStudentCode(request.getStudentCode().trim());
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim()); 
        student.setEmail(request.getEmail().toLowerCase());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress().trim());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());

        Student updateStudent = studentRepository.save(student);
        StudentResponse response = StudentResponse.fromEntity(updateStudent); 
        redisService.set(
            STUDENT_KEY_PREFIX + id, 
            response, 
            STUDENT_CACHE_TTL);
        redisService.delete(ALL_STUDENTS_KEY);
        return response;
    }
    @Override
    @CacheEvict( value = "students", key = "#id")
    public void deleteStudent(Long id) {
        Student student = findStudent(id); 
        studentRepository.delete(student);
        redisService.delete(
            STUDENT_KEY_PREFIX + id
        );
        redisService.delete(ALL_STUDENTS_KEY);
    }
    private Student findStudent(Long id){
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student Not Found with ID : " + id
            ));
    }
}
