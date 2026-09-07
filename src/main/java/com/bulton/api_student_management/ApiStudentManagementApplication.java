package com.bulton.api_student_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching 
public class ApiStudentManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiStudentManagementApplication.class, args);
	}

}
