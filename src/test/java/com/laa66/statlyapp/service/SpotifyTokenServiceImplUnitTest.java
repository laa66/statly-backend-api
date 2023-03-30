package com.laa66.statlyapp.service;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.AccessToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyTokenServiceImplUnitTest {

    static OAuth2AuthorizedClient client;

    @Mock
    RestTemplate restTemplate;

    @InjectMocks
    SpotifyTokenServiceImpl spotifyTokenService;

    @BeforeAll
    static void prepare() {
        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId("client")
                .clientName("client")
                .clientId("CLIENT_ID")
                .clientSecret("CLIENT_SECRET")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope("SCOPE")
                .authorizationUri("/authorize")
                .tokenUri("/token")
                .redirectUri("/redirect")
                .userInfoUri("/me")
                .userNameAttributeName("name")
                .build();
        OAuth2AccessToken accessToken =
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access", Instant.EPOCH, Instant.EPOCH.plusSeconds(10));
        OAuth2RefreshToken refreshToken =
                new OAuth2RefreshToken("refresh", Instant.EPOCH, Instant.EPOCH.plusSeconds(30));
        client = new OAuth2AuthorizedClient(clientRegistration, "user", accessToken, refreshToken);
    }

    @Test
    void shouldRefreshAccessToken() {
        AccessToken accessToken = new AccessToken("new", "BEARER", "SCOPE", 10);
        when(restTemplate.exchange(eq(SpotifyAPI.TOKEN_ENDPOINT),
                eq(HttpMethod.POST),any() , eq(AccessToken.class)))
                .thenReturn(new ResponseEntity<>(accessToken, HttpStatus.CREATED));

        OAuth2AccessToken refreshedToken = spotifyTokenService.refreshAccessToken(client);
        assertEquals(OAuth2AccessToken.TokenType.BEARER, refreshedToken.getTokenType(), "Token type should be BEARER");
        assertNotEquals(client.getAccessToken().getTokenValue(), refreshedToken.getTokenValue(), "Returned Token should have different value");
    }
}