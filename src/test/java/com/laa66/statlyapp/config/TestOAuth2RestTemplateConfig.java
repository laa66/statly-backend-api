package com.laa66.statlyapp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestOAuth2RestTemplateConfig {

    @Bean
    public RestTemplate restTemplateInterceptor() {
        return new RestTemplate();
    }
}
