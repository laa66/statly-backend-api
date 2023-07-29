package com.laa66.statlyapp.repository.impl;

import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Optional;

@AllArgsConstructor
public class SpotifyTokenRepositoryImpl implements SpotifyTokenRepository {

    private final CacheManager cacheManager;

    @Override
    public OAuth2AuthenticationToken getToken(long userId) {
        return Optional.ofNullable(cacheManager.getCache("api_token"))
                .map(cache -> cache.get(userId, OAuth2AuthenticationToken.class))
                .orElseThrow(() -> new UserAuthenticationException("API token repository does not exists or user not found"));
    }

    @Override
    public void saveToken(long userId, OAuth2AuthenticationToken token) {
        Optional.ofNullable(cacheManager.getCache("api_token"))
                .ifPresentOrElse(cache -> cache.put(userId, token),
                        () -> {
                    throw new UserAuthenticationException("API token repository does not exists");
                });
    }

    @Override
    public void removeToken(long userId) {
        Optional.ofNullable(cacheManager.getCache("api_token"))
                .ifPresentOrElse(cache -> cache.evictIfPresent(userId),
                        () -> {
                    throw new UserAuthenticationException("API token repository does not exists");
                        });
    }
}
