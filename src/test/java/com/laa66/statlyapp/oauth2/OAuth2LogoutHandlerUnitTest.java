package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.impl.SpotifyTokenRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuth2LogoutHandlerUnitTest {

    @Mock
    SpotifyTokenRepositoryImpl spotifyTokenRepository;

    @InjectMocks
    OAuth2LogoutHandler oAuth2LogoutHandler;

    MockHttpServletRequest request;
    MockHttpServletResponse response;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void shouldLogoutAuthenticated() {
        OAuth2UserWrapper principal = new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of(
                        "display_name", "name", "userId", 1L
        ), "display_name"));
        Authentication authentication = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "client");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        oAuth2LogoutHandler.logout(request, response, authentication);
        verify(spotifyTokenRepository, times(1)).removeToken(1L);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldLogoutNotAuthenticated() {
        assertDoesNotThrow(() -> oAuth2LogoutHandler.logout(request, response, null));
        verifyNoInteractions(spotifyTokenRepository);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldLogoutTokenRepositoryException() {
        OAuth2UserWrapper principal = new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of(
                "display_name", "name", "userId", 1L
        ), "display_name"));
        Authentication authentication = new OAuth2AuthenticationToken(principal, principal.getAuthorities(), "client");
        SecurityContextHolder.getContext().setAuthentication(authentication);
        doThrow(UserAuthenticationException.class).when(spotifyTokenRepository).removeToken(1L);
        assertDoesNotThrow(() -> oAuth2LogoutHandler.logout(request, response, authentication));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}