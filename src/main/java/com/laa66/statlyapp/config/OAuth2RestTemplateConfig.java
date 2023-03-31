package com.laa66.statlyapp.config;

import com.laa66.statlyapp.interceptor.HeaderModifierTokenRefresherInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OAuth2RestTemplateConfig {

    /**
     *  Rest template bean with interceptor for adding
     *  header and checking if tokens need to be refreshed.
     *  If exchange status code is 401 - send request to refresh token,
     *  re-authenticate user and repeat request.
     *
     **/

    @Bean("restTemplateInterceptor")
    public RestTemplate restTemplateInterceptor() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(interceptor());
        return restTemplate;
    }

    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HeaderModifierTokenRefresherInterceptor interceptor() {
        return new HeaderModifierTokenRefresherInterceptor();
    }

}
