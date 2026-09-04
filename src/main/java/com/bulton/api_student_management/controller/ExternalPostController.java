package com.bulton.api_student_management.controller;

import com.bulton.api_student_management.dto.response.ApiResponse;
import com.bulton.api_student_management.dto.response.ExternalPostResponse;
import com.bulton.api_student_management.service.ExternalPostService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external/posts")
@RequiredArgsConstructor
@Validated
public class ExternalPostController {

    private final ExternalPostService externalPostService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExternalPostResponse>> getPostById(
        @PathVariable
        @Positive(message = "Post ID must be greater than zero")
        Long id
    ) {
        ExternalPostResponse post =
            externalPostService.getPostById(id);

        ApiResponse<ExternalPostResponse> response =
            ApiResponse.success(
                HttpStatus.OK.value(),
                "External post retrieved successfully",
                post
            );

        return ResponseEntity.ok(response);
    }
}