package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.exception.BadRequestException;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.impl.SpotifyTokenRepositoryImpl;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OAuth2SuccessHandlerUnitTest {

    @Mock
    SpotifyTokenRepositoryImpl spotifyTokenRepository;;

    @Mock
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Mock
    JwtProvider jwtProvider;

    OAuth2SuccessHandler oAuth2SuccessHandler;

    MockHttpServletRequest request;

    MockHttpServletResponse response;

    Authentication authentication;

    @BeforeEach
    void setup() {
        oAuth2SuccessHandler = new OAuth2SuccessHandler(
                spotifyTokenRepository,
                httpCookieOAuth2AuthorizationRequestRepository,
                jwtProvider,
                URI.create("http://localhost:3000")
        );
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        authentication = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of(
                        "userId", 1L, "display_name", "name"
        ), "display_name")), Collections.emptyList(),"clientId");
    }

    @Test
    void shouldOnAuthenticationSuccessValidRedirectUri() throws IOException {
        request.setCookies(new Cookie("redirect_uri", "http://localhost:3000"));
        when(jwtProvider.createToken(1)).thenReturn("header.payload.signature");
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals("http://localhost:3000?jwt=header.payload.signature", response.getRedirectedUrl());
        assertNotNull(request.getCookies());
    }

    @Test
    void shouldOnAuthenticationSuccessEmptyRedirectUri() throws IOException {
        when(jwtProvider.createToken(1)).thenReturn("header.payload.signature");
        oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication);
        assertEquals("/?jwt=header.payload.signature", response.getRedirectedUrl());
    }

    @Test
    void shouldOnAuthenticationSuccessNotValidRedirectUri() {
        request.setCookies(new Cookie("redirect_uri", "http://www.somedomain.com/next"));
        assertThrows(BadRequestException.class, () -> oAuth2SuccessHandler.onAuthenticationSuccess(request, response, authentication));
    }

}