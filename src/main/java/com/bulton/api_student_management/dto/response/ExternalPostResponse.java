package com.bulton.api_student_management.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExternalPostResponse {

    private Long userId;
    private Long id;
    private String title;
    private String body;
}