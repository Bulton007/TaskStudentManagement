package com.bulton.api_student_management.service.impl;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bulton.api_student_management.audit.entity.StudentOperationType;
import com.bulton.api_student_management.audit.service.StudentAuditService;
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
@Transactional (
    transactionManager = "transactionManager"
)
public class StudentServiceImpl implements StudentService{
    private final StudentRepository studentRepository;
    private final RedisService redisService;
    private final StudentAuditService studentAuditService;
    private static final String STUDENT_KEY_PREFIX = "students:"; 
    private static final String ALL_STUDENTS_KEY = "students:all"; 
    private static final Duration STUDENT_CACHE_TTL = Duration.ofMinutes(10); 
    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if(studentRepository.existsByStudentCode(request.getStudentCode())){
            throw new DuplicationResourceException(
                "Student Code Already Exits" + request.getStudentCode()
            ); 
        }

        if(studentRepository.existsByEmail(request.getEmail())){
            throw new DuplicationResourceException(
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
        studentAuditService.recordOperation(
    savedStudent.getId(),
    StudentOperationType.CREATE,
    getCurrentUsername(),
    "Student created: "
        + savedStudent.getFirstName()
        + " "
        + savedStudent.getLastName()
);

// TEMPORARY LOCAL XA TEST — remove after testing.
if ("XA-FINAL-001".equals(savedStudent.getStudentCode())) {
    throw new IllegalStateException(
        "XA_FINAL_TEST: failure after audit flush, before global commit"
    );
}
        redisService.set(
            STUDENT_KEY_PREFIX + savedStudent.getId(), 
            response, 
            STUDENT_CACHE_TTL);
        redisService.delete(ALL_STUDENTS_KEY);
        return response;    
    }
    @Override
    @Transactional(readOnly = true, 
        transactionManager = "transactionManager"
    )
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
                studentAuditService.recordOperation(
            id,
            StudentOperationType.READ,
            getCurrentUsername(),
            "Student Viewd: "+ id
                          
        );
        redisService.set(cacheKey, response, Duration.ofMinutes(10));
        return response;
    }
    @Override
    @Cacheable(value = "studentList", key = "'all'")
    public List<StudentResponse> getAllStudents() {
        Optional<StudentResponse[]> cached = 
            redisService.get(
                ALL_STUDENTS_KEY, 
                StudentResponse[].class);
        List<StudentResponse> response; 
        if(cached.isPresent()){
            response = Arrays.asList(cached.get());
        }else{
            response = 
                studentRepository.findAll()
                    .stream()
                    .map(StudentResponse::fromEntity)
                    .toList();
            redisService.set(
                ALL_STUDENTS_KEY, 
                response, 
                STUDENT_CACHE_TTL);
        }
        studentAuditService.recordOperation(
        null, 
        StudentOperationType.READ_ALL,
        getCurrentUsername(),
        "All Students Viewed; total = " + response.size()
    );
    return response;
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
        
                studentAuditService.recordOperation(
            updateStudent.getId(), 
            StudentOperationType.CREATE,
            getCurrentUsername(),
            "Student updated: "
                            + updateStudent.getFirstName()
                            + " " 
                            + updateStudent.getLastName()    
        );
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
        studentRepository.flush();
        String studentName = student.getFirstName() + " " + student.getLastName();
        redisService.delete(
            STUDENT_KEY_PREFIX + id
        );
                studentAuditService.recordOperation(
            id,
            StudentOperationType.DELETE,
            getCurrentUsername(),
            "Student created: " + studentName
                                
        );
        redisService.delete(ALL_STUDENTS_KEY);
    }
    private Student findStudent(Long id){
        return studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                "Student Not Found with ID : " + id
            ));
    }
    private String getCurrentUsername(){
        Authentication authentication = 
            SecurityContextHolder
                .getContext()
                .getAuthentication();
            if( authentication == null
                    || !authentication.isAuthenticated()
                    || "anonymousUser".equals(
                        authentication.getPrincipal()
                    )
            ){
                return "anonymous";
            }
        return authentication.getName();
    }
}
