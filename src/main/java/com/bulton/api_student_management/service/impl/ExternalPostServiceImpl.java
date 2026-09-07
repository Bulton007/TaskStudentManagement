package com.bulton.api_student_management.service.impl;

import com.bulton.api_student_management.client.GlobalThirdPartyClient;
import com.bulton.api_student_management.dto.response.ExternalPostResponse;
import com.bulton.api_student_management.service.ExternalPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalPostServiceImpl implements ExternalPostService {

    private final GlobalThirdPartyClient thirdPartyClient;

    @Override
    public ExternalPostResponse getPostById(Long id) {
        return thirdPartyClient
            .get(
                "json-placeholder", 
                "/posts/" + id, 
                ExternalPostResponse.class
            );
    }
}