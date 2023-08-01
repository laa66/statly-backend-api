package com.laa66.statlyapp.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public interface SpotifyTokenService {
    OAuth2AuthenticationToken getToken(long userId);
    void saveToken(long userId, OAuth2AuthenticationToken token);
    void removeToken(long userId);
    OAuth2AccessToken refreshAccessToken(OAuth2AuthorizedClient client);
}
