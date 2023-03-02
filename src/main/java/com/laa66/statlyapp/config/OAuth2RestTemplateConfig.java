package com.laa66.statlyapp.config;

import com.laa66.statlyapp.service.SpotifyTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;

@Configuration
public class OAuth2RestTemplateConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2RestTemplateConfig.class);

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private SpotifyTokenService tokenService;

    /**
     *  Rest template bean with interceptor for adding
     *  header and checking if tokens need to be refreshed.
     *  If exchange status code is 401 - send request to refresh token.
     *
     **/
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) throw new RuntimeException("User not authenticated");
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
            System.out.println("---> Auth User: " + client.getPrincipalName() + ", Token: " + client.getAccessToken().getTokenValue() + "Expires at: " + client.getAccessToken().getExpiresAt().atZone(ZoneId.systemDefault()) +", Refresh token: " + client.getRefreshToken().getTokenValue());

            if (client == null) throw new RuntimeException("Client is null");
            request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
            ClientHttpResponse response = execution.execute(request, body);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                LOGGER.info("---> Spotify API access forbidden, refreshing token and sending new request...");
                // get refreshed token
                OAuth2AccessToken accessToken = tokenService.getNewAccessToken(client);

                // re-authenticate user
                OAuth2AuthorizedClient newAuthorizedClient = new OAuth2AuthorizedClient(client.getClientRegistration(),
                        client.getPrincipalName(), accessToken, client.getRefreshToken());
                Authentication newPrincipal = new OAuth2AuthenticationToken(token.getPrincipal(), token.getAuthorities(), token.getAuthorizedClientRegistrationId());
                clientService.removeAuthorizedClient(token.getAuthorizedClientRegistrationId(), client.getPrincipalName());
                clientService.saveAuthorizedClient(newAuthorizedClient, newPrincipal);

                // execute request with refreshed token
                request.getHeaders().clearContentHeaders();
                request.getHeaders().setBearerAuth(accessToken.getTokenValue());
                response = execution.execute(request, body);
            }

            return response;
        });
        return restTemplate;
    }

}
