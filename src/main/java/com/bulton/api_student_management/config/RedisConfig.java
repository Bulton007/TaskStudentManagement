package com.bulton.api_student_management.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import tools.jackson.databind.ObjectMapper;


@Configuration 
public class RedisConfig {
    @Bean  
    public RedisConnectionFactory redisConnectionFactory(
        @Value ("${spring.data.redis.host}")
        String host, 

        @Value ("${spring.data.redis.port}")
        int port, 

        @Value ("${spring.data.redis.database:0}")
        int database, 

        @Value("${spring.data.redis.password:}")
        String password
    ){
        RedisStandaloneConfiguration configuration = 
            new RedisStandaloneConfiguration(); 
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(database);
        if(StringUtils.hasText(password)){
            configuration.setPassword(
                RedisPassword.of(password)
            );
        }
        return new LettuceConnectionFactory(
            configuration
        );
    }
    @Bean 
    public RedisTemplate<String,Object> redisTemplate(
        RedisConnectionFactory connectionFactory, 
        ObjectMapper objectMapper
    ){
        RedisTemplate<String, Object> template =
            new RedisTemplate<>();
        StringRedisSerializer keySerializer = 
            new StringRedisSerializer(); 
        JacksonJsonRedisSerializer<Object> valuSerializer = 
            new JacksonJsonRedisSerializer<>(
                objectMapper, 
                Object.class
            ); 
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);
        template.setValueSerializer(valuSerializer);
        template.setHashValueSerializer(valuSerializer);
        template.afterPropertiesSet();
        return template;
             
    }
}
