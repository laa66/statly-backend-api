package com.laa66.statlyapp.interceptor;

import com.laa66.statlyapp.exception.ClientAuthorizationException;
import com.laa66.statlyapp.exception.EmptyTokenException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.service.SpotifyTokenService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class HeaderModifierTokenRefresherInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientService clientService;
    private final SpotifyTokenService tokenService;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        Authentication authentication = getAuthentication();
        OAuth2AuthorizedClient client = getAuthorizedClient(authentication);
        OAuth2AccessToken accessToken = getAccessToken(client);
        setBearerToken(request, accessToken);

        try {
            ClientHttpResponse response = execution.execute(request, body);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                log.info("-->> Spotify API access forbidden, refreshing token and sending new request...");
                OAuth2AccessToken newAccessToken = refreshAccessToken(client);
                OAuth2AuthorizedClient newAuthorizedClient = createAuthorizedClient(client, newAccessToken);
                OAuth2AuthenticationToken newAuthentication = createOAuth2AuthenticationToken(authentication);
                updateAuthorizedClient(client, newAuthorizedClient, newAuthentication);
                tokenService.saveToken(((OAuth2UserWrapper) authentication.getPrincipal()).getUserId(), newAuthentication);

                request.getHeaders().clearContentHeaders();
                setBearerToken(request, newAccessToken);
                response = execution.execute(request, body);
            } else if (response.getStatusCode().isError()) {
                throw new SpotifyAPIException("Spotify API error", response.getStatusCode().value());
            }
            return response;
        } catch (IOException e) {
            throw new SpotifyAPIException("Server cannot reach Spotify API", HttpStatus.SERVICE_UNAVAILABLE.value());
        }
    }

    //helpers
    private Authentication getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .orElseThrow(() -> new UserAuthenticationException("User not authenticated"));
    }

    private OAuth2AuthorizedClient getAuthorizedClient(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(token.getAuthorizedClientRegistrationId(), token.getName());
        return Optional.ofNullable(client)
                .orElseThrow(() -> new ClientAuthorizationException("Client cannot be loaded"));
    }

    private OAuth2AccessToken getAccessToken(OAuth2AuthorizedClient client) {
        return Optional.ofNullable(client.getAccessToken())
                .orElseThrow(() -> new EmptyTokenException("User access token is missing"));
    }

    private OAuth2AccessToken refreshAccessToken(OAuth2AuthorizedClient client) {
        return tokenService.refreshAccessToken(client);
    }

    private OAuth2AuthorizedClient createAuthorizedClient(OAuth2AuthorizedClient client, OAuth2AccessToken newAccessToken) {
        return new OAuth2AuthorizedClient(
                client.getClientRegistration(),
                client.getPrincipalName(),
                newAccessToken,
                client.getRefreshToken()
        );
    }

    private OAuth2AuthenticationToken createOAuth2AuthenticationToken(Authentication authentication) {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        return new OAuth2AuthenticationToken(
                token.getPrincipal(),
                token.getAuthorities(),
                token.getAuthorizedClientRegistrationId()
        );
    }

    private void setBearerToken(HttpRequest request, OAuth2AccessToken accessToken) {
        request.getHeaders().setBearerAuth(accessToken.getTokenValue());
    }

    private void updateAuthorizedClient(OAuth2AuthorizedClient oldClient, OAuth2AuthorizedClient newClient, Authentication newAuthentication) {
        clientService.removeAuthorizedClient(oldClient.getClientRegistration().getRegistrationId(), oldClient.getPrincipalName());
        clientService.saveAuthorizedClient(newClient, newAuthentication);
    }
}
