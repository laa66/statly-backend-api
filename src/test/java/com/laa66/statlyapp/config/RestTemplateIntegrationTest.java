package com.laa66.statlyapp.config;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.interceptor.HeaderModifierTokenRefresherInterceptor;
import com.laa66.statlyapp.service.SpotifyTokenServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = {TestSecurityConfig.class, OAuth2RestTemplateConfig.class})
class RestTemplateIntegrationTest {

    @Autowired
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @Autowired
    HeaderModifierTokenRefresherInterceptor interceptor;

    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    OAuth2AuthorizedClientService clientService;

    @MockBean
    SpotifyTokenServiceImpl spotifyTokenService;

    @Mock
    OAuth2AuthenticationToken authentication;

    MockRestServiceServer mockServer;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
        OAuth2AccessToken accessToken =
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "access", Instant.EPOCH, Instant.EPOCH.plusSeconds(10));
        OAuth2RefreshToken refreshToken =
                new OAuth2RefreshToken("refresh", Instant.EPOCH, Instant.EPOCH.plusSeconds(30));
        OAuth2AccessToken newAccessToken =
                new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "newAccess", Instant.EPOCH, Instant.EPOCH.plusSeconds(10));
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("spotify");
        when(authentication.getName()).thenReturn("user");
        when(authentication.getPrincipal()).thenReturn(new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("USER")), Map.of("name", "test"), "name"));
        when(authentication.getAuthorities()).thenReturn(List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(clientService.loadAuthorizedClient("spotify", "user"))
                .thenReturn(new OAuth2AuthorizedClient(clientRegistrationRepository.findByRegistrationId("spotify"),
                        "user", accessToken, refreshToken));
        when(spotifyTokenService.refreshAccessToken(any())).thenReturn(newAccessToken);
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSetActiveToken() {
        String data = "body";
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(data));
        String response = restTemplate.getForObject(SpotifyAPI.CURRENT_USER, String.class);
        mockServer.verify();
        assertEquals(data, response);
    }

    @Test
    void shouldRefreshToken() {
        String data = "body";
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access"))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer newAccess"))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(data));
        String response = restTemplate.getForObject(SpotifyAPI.CURRENT_USER, String.class);
        mockServer.verify();
        assertEquals(data, response);
    }

    @Test
    void shouldThrowException() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer access"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(SpotifyAPIException.class,
                () -> restTemplate.getForObject(SpotifyAPI.CURRENT_USER, String.class));
        mockServer.verify();
    }

}