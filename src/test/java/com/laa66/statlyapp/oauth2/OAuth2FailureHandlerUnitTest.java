package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.exception.UserAuthenticationException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OAuth2FailureHandlerUnitTest {

    @Mock
    HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @InjectMocks
    OAuth2FailureHandler oAuth2FailureHandler;

    MockHttpServletResponse response;

    MockHttpServletRequest request;

    AuthenticationException exception;

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        exception = new UserAuthenticationException("User cannot be properly authenticated");
    }

    @Test
    void shouldOnAuthenticationFailureWithRedirectUri() throws IOException {
        request.setCookies(new Cookie("redirect_uri", "http://localhost:3000"));
        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);
        assertEquals("http://localhost:3000?error=User cannot be properly authenticated", response.getRedirectedUrl());
    }

    @Test
    void shouldOnAuthenticationFailureWithoutRedirectUri() throws Exception {
        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);
        assertEquals("/?error=User cannot be properly authenticated", response.getRedirectedUrl());
    }
}