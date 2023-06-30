package com.laa66.statlyapp.service;

import com.laa66.statlyapp.config.CustomOAuth2UserService;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceUnitTest {

    @Mock
    UserService userService;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

    @Test
    void shouldGetUser() {
        User user = new User(1L, "id","username", "test@mail.com", "url", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singletonList(new OAuth2UserAuthority(Map.of("user","user"))),
                Map.of(
                        "display_name", "user", "email", "test@mail.com",
                        "images", List.of(), "id", "id"),
                "display_name"
                );
        when(userService.findUserByEmail("test@mail.com")).thenReturn(Optional.of(user));
        OAuth2User result = customOAuth2UserService.getUserOrCreate(oAuth2User);
        assertNotNull(result);
        assertEquals(oAuth2User.getAuthorities(), result.getAuthorities());
        assertEquals(oAuth2User.getAttributes().get("email"), result.getAttributes().get("email"));
        assertEquals(1L, result.getAttributes().get("userId"));
        assertEquals(oAuth2User.getAttributes().get("display_name"), result.getAttributes().get("display_name"));
    }

    @Test
    void shouldCreateUser() {
        User user = new User(0, "id", "username","test@mail.com", "url", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        User createdUser = new User(1L, "id", "username", "test@mail.com", "url", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        OAuth2User oAuth2User = new DefaultOAuth2User(
                Collections.singletonList(new OAuth2UserAuthority(Map.of("user","user"))),
                Map.of(
                        "display_name", "user", "email", "test@mail.com",
                        "images", List.of(Map.of("url", "imageUrl")), "id", "id"),
                "display_name"
                );
        when(userService.findUserByEmail("test@mail.com")).thenReturn(Optional.empty()).thenReturn(Optional.of(createdUser));
        when(userService.saveUser(any())).thenReturn(createdUser);
        OAuth2User result = customOAuth2UserService.getUserOrCreate(oAuth2User);
        assertNotNull(result);
        assertEquals(oAuth2User.getAuthorities(), result.getAuthorities());
        assertEquals(oAuth2User.getAttributes().get("email"), result.getAttributes().get("email"));
        assertEquals(1L, result.getAttributes().get("userId"));
        assertEquals(oAuth2User.getAttributes().get("display_name"), result.getAttributes().get("display_name"));
    }

}