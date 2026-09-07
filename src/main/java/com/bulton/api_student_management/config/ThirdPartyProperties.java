package com.bulton.api_student_management.config;

import java.security.Provider;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "third-party")
public class ThirdPartyProperties {
    private Map<String, Provider> providers = new HashMap<>();
    public Map<String, Provider> getProviders(){
        return providers; 
    }
    public void setProviders(Map<String, Provider> providers ){
        this.providers = providers; 
    }
    public static class Provider{
        private String baseUrl; 
        private Duration timeout = Duration.ofSeconds(5);
        public String getBaseUrl(){
            return baseUrl;
        }
        public void setBaseUrl(String baseUrl){
            this.baseUrl = baseUrl;
        }
        public Duration getTimeout(){
            return timeout;
        }
        public void setTimeout(Duration timemout){
            this.timeout = timemout;
        }
    }
}
