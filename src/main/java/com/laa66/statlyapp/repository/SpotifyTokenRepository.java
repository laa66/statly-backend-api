package com.laa66.statlyapp.repository;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public interface SpotifyTokenRepository {

    OAuth2AuthenticationToken getToken(long userId);
    void saveToken(long userId, OAuth2AuthenticationToken token);
    void removeToken(long userId);
}
