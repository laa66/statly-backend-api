package com.laa66.statlyapp.service;

import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public interface SpotifyTokenService {
    OAuth2AccessToken refreshAccessToken(OAuth2AuthorizedClient client);
}
