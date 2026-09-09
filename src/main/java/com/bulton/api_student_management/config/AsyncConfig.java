package com.bulton.api_student_management.config;

import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

@Configuration 
@EnableAsync 
public class AsyncConfig {
    @Bean (name = "studentTaskExecutor")
    public ThreadPoolTaskExecutor studentTaskExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); 

        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);

        executor.setThreadNamePrefix("student-async");

        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.AbortPolicy()
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }

    @Bean ( name = "securedStudentTaskExecutor")
    public AsyncTaskExecutor securedStudentTaskExecutor(
        @Qualifier ("studentTaskExecutor")
        ThreadPoolTaskExecutor executor
    ){
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }
}
