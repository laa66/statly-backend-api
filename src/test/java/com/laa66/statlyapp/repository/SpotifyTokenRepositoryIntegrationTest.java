package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.config.CacheConfig;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.repository.impl.SpotifyTokenRepositoryImpl;
import com.laa66.statlyapp.service.StatsService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {CacheConfig.class, SpotifyTokenRepositoryImpl.class})
@MockBean(value = StatsService.class)
class SpotifyTokenRepositoryIntegrationTest {

    @Autowired
    SpotifyTokenRepository spotifyTokenRepository;

    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setup() {
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
                new DefaultOAuth2User(Collections.emptyList(), Map.of("username", "user1"), "username"),
                Collections.emptySet(),
                "client");
        spotifyTokenRepository.saveToken(1, token);
    }

    @AfterEach
    void after() {
        Cache cache = cacheManager.getCache("api_token");
        assertNotNull(cache);
        cache.invalidate();
    }

    @Test
    void shouldGetToken() {
        OAuth2AuthenticationToken returned = spotifyTokenRepository.getToken(1);
        assertEquals("user1", returned.getName());
        assertEquals("client", returned.getAuthorizedClientRegistrationId());
        assertThrows(UserAuthenticationException.class, () -> spotifyTokenRepository.getToken(2));
    }

    @Test
    void shouldSaveToken() {
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(
                new DefaultOAuth2User(Collections.emptyList(), Map.of("username", "user2"), "username"),
                Collections.emptySet(),
                "client");
        spotifyTokenRepository.saveToken(2, token);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                ((CaffeineCache) Objects.requireNonNull(cacheManager.getCache("api_token"))).getNativeCache();
        assertEquals(2, nativeCache.asMap().size());
        assertEquals("user2", spotifyTokenRepository.getToken(2).getName());
        assertEquals("user1", spotifyTokenRepository.getToken(1).getName());
    }

    @Test
    void shouldRemoveToken() {
        spotifyTokenRepository.removeToken(1);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                ((CaffeineCache) Objects.requireNonNull(cacheManager.getCache("api_token"))).getNativeCache();
        assertEquals(0, nativeCache.asMap().size());
    }


}
