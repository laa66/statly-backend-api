package com.laa66.statlyapp.jwt;

import com.laa66.statlyapp.repository.impl.SpotifyTokenRepositoryImpl;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterUnitTest {

    @Mock
    JwtProvider jwtProvider;

    @Mock
    SpotifyTokenRepositoryImpl spotifyTokenRepository;

    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    MockFilterChain filterChain;

    OAuth2AuthenticationToken authenticationToken;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldDoFilterInternalPresentValidToken() throws ServletException, IOException {
        authenticationToken = mock(OAuth2AuthenticationToken.class);
        String token = "token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtProvider.validateToken(token)).thenReturn(true);
        when(jwtProvider.getIdFromToken(token)).thenReturn(1L);
        when(spotifyTokenRepository.getToken(1L)).thenReturn(authenticationToken);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtProvider, times(1)).validateToken(token);
        verify(jwtProvider, times(1)).getIdFromToken(token);
        verify(spotifyTokenRepository, times(1)).getToken(1L);
        verify(authenticationToken, times(1)).setDetails(any());
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authenticated);
    }

    @Test
    void shouldDoFilterInternalMissingToken() throws ServletException, IOException {
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authenticated);
    }

    @Test
    void shouldDoFilterInternalPresentNotValidToken() throws ServletException, IOException {
        String token = "token";
        request.addHeader("Authorization", "Bearer " + token);
        when(jwtProvider.validateToken(token)).thenReturn(false);
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
        verify(jwtProvider, times(1)).validateToken(token);
        Authentication authenticated = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authenticated);
    }
}