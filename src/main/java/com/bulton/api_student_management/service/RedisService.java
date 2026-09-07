package com.bulton.api_student_management.service;

import java.time.Duration;
import java.util.Optional;

public interface RedisService {
    void set(
        String key, 
        Object value, 
        Duration timeout
    ); 
    
    <T> Optional<T> get(
        String key, 
        Class<T> responseType
    ); 
    
    boolean exitst(String key); 
    void delete(String key); 
    Duration getTimeToLive(String key); 
}
