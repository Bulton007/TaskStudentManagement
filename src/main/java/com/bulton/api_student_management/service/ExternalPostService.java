package com.bulton.api_student_management.service;

import com.bulton.api_student_management.dto.response.ExternalPostResponse;

public interface ExternalPostService {

    ExternalPostResponse getPostById(Long id);
}