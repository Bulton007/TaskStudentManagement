package com.bulton.api_student_management.service.impl;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.bulton.api_student_management.service.RedisService;

import lombok.RequiredArgsConstructor;
import tools.jackson.databind.ObjectMapper;

@Service 
@RequiredArgsConstructor 
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate; 
    private final ObjectMapper objectMapper;
    @Override
    public void set(String key, Object value, Duration timeout) {
        redisTemplate
            .opsForValue()
            .set(key,value, timeout);
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> responseType) {
        Object value = redisTemplate
            .opsForValue()
            .get(key); 
        if( value == null){
            return Optional.empty(); 
        }
        if (responseType.isInstance(value)){
            return Optional.of(
                responseType.cast(value)
            );
        }
        T convertedValue = objectMapper.convertValue(value, responseType); 
        return Optional.of(convertedValue);
    }

    @Override
    public boolean exitst(String key) {
        return Boolean.TRUE.equals(
            redisTemplate.hasKey(key)
        ); 
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Duration getTimeToLive(String key) {
        Long seconds = redisTemplate.getExpire(
            key, 
            TimeUnit.SECONDS
        ); 
        if( seconds == null || seconds < 0){
            return  Duration.ZERO; 
        }
        return Duration.ofSeconds(seconds);
    }
}
