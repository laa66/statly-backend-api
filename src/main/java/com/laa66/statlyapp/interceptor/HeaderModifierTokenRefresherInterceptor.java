package com.laa66.statlyapp.interceptor;

import com.laa66.statlyapp.exception.ClientAuthorizationException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.service.SpotifyTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;


@Slf4j
public class HeaderModifierTokenRefresherInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientService clientService;
    private final SpotifyTokenService tokenService;

    public HeaderModifierTokenRefresherInterceptor(OAuth2AuthorizedClientService clientService, SpotifyTokenService tokenService) {
        this.clientService = clientService;
        this.tokenService = tokenService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) throw new UserAuthenticationException("User not authenticated");
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());

        if (client == null) throw new ClientAuthorizationException("Client is null //then logout user");
        request.getHeaders().setBearerAuth(client.getAccessToken().getTokenValue());
        ClientHttpResponse response = execution.execute(request, body);

        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            log.info("-->> Spotify API access forbidden, refreshing token and sending new request...");
            // get refreshed token
            OAuth2AccessToken accessToken = tokenService.refreshAccessToken(client);

            // re-authenticate user
            OAuth2AuthorizedClient newAuthorizedClient = new OAuth2AuthorizedClient(client.getClientRegistration(),
                    client.getPrincipalName(), accessToken, client.getRefreshToken());
            Authentication newPrincipal = new OAuth2AuthenticationToken(token.getPrincipal(), token.getAuthorities(), token.getAuthorizedClientRegistrationId());
            clientService.removeAuthorizedClient(token.getAuthorizedClientRegistrationId(), client.getPrincipalName());
            clientService.saveAuthorizedClient(newAuthorizedClient, newPrincipal);

            // execute request with new token
            request.getHeaders().clearContentHeaders();
            request.getHeaders().setBearerAuth(accessToken.getTokenValue());
            response = execution.execute(request, body);
        } else if (response.getStatusCode().isError()) {
            throw new SpotifyAPIException("Spotify API error", response.getStatusCode().value());
        }
        return response;
    }
}
