package com.laa66.statlyapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OAuth2RestTemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2RestTemplateConfig.class);

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    /**
     *  Rest template bean with interceptor for adding
     *  header and checking if tokens need to be refreshed.
     *  If response status code is other than 200 OK - send request to refresh token.
     *
     */
    // TODO: 22.02.2023 If user is null try to logout him
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return execution.execute(request, body);
            }
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = oAuth2AuthorizedClientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());

            if (client == null) {
                LOGGER.info("If client is null try to logout user.");
                return execution.execute(request, body);
            }

            request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
            ClientHttpResponse response = execution.execute(request, body);
            if (!response.getStatusCode().is2xxSuccessful()) LOGGER.info("Token needs refreshing - " + response.getStatusCode());
            return response;
        });
        return restTemplate;
    }

}
