package com.laa66.statlyapp.service;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.AccessToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class SpotifyTokenServiceImpl implements SpotifyTokenService {

    private final RestTemplate restTemplate;

    public SpotifyTokenServiceImpl(@Qualifier("restTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(OAuth2AuthorizedClient client) {
        AccessToken refreshedToken = postRefreshTokenRequest(client);
        String[] scopeArr = refreshedToken.getScope().split(" ");
        Set<String> scopes = new HashSet<>(Arrays.stream(scopeArr).toList());
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, refreshedToken.getAccessToken(), Instant.now(),
                Instant.now().plusSeconds(refreshedToken.getExpiresIn()), scopes);
    }

    // helpers
    private AccessToken postRefreshTokenRequest(OAuth2AuthorizedClient client) {
        MultiValueMap<String, String> refreshBody = new LinkedMultiValueMap<>();
        refreshBody.add("grant_type", "refresh_token");
        refreshBody.add("refresh_token", client.getRefreshToken().getTokenValue());
        String encoded = Base64.getEncoder().encodeToString((client.getClientRegistration().getClientId() + ":" + client.getClientRegistration().getClientSecret()).getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(encoded);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> entity = new HttpEntity<>(refreshBody, headers);
        ResponseEntity<AccessToken> response = restTemplate.exchange(SpotifyAPI.TOKEN_ENDPOINT, HttpMethod.POST, entity, AccessToken.class);
        return response.getBody();
    }
}
