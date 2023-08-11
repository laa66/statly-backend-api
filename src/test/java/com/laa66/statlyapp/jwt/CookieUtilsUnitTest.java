package com.laa66.statlyapp.jwt;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.SerializationUtils;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import static com.laa66.statlyapp.jwt.CookieUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class CookieUtilsUnitTest {

    @Test
    void shouldGetCookie() {
        MockHttpServletRequest request1 = new MockHttpServletRequest();
        Cookie cookie = new Cookie("Auth", "value");
        request1.setCookies(cookie);
        Optional<Cookie> auth1 = getCookie(request1, "Auth");
        assertTrue(auth1.isPresent());
        assertEquals(cookie, auth1.get());

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        Optional<Cookie> auth2 = getCookie(request2, "Auth");
        assertTrue(auth2.isEmpty());
    }

    @Test
    void shouldAddCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        addCookie(response, "Auth", "value", 60);
        Cookie[] cookies = response.getCookies();
        assertEquals(1, cookies.length);
        assertEquals("value", cookies[0].getValue());
        assertEquals("/", cookies[0].getPath());
        assertEquals(60, cookies[0].getMaxAge());
    }

    @Test
    void shouldDeleteCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("Auth", "value"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        deleteCookie(request, response, "Auth");
        Cookie[] cookies = response.getCookies();
        assertEquals(1, cookies.length);
        assertEquals("", cookies[0].getValue());
        assertEquals("Auth", cookies[0].getName());
        assertEquals(0, cookies[0].getMaxAge());

    }

    @Test
    void shouldSerialize() {
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("uri")
                .clientId("client")
                .redirectUri("redirect_uri")
                .scope("USER")
                .authorizationRequestUri("request_uri")
                .state("state")
                .build();
        String serialized = Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(authorizationRequest));
        String result = serialize(authorizationRequest);
        assertEquals(serialized, result);
    }

    @Test
    void shouldDeserialize() {
        OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
                .authorizationUri("uri")
                .clientId("client")
                .redirectUri("redirect_uri")
                .scope("USER")
                .authorizationRequestUri("request_uri")
                .state("state")
                .build();
        String value = serialize(authorizationRequest);
        Cookie cookie = new Cookie("Auth", value);
        OAuth2AuthorizationRequest returned = deserialize(cookie, OAuth2AuthorizationRequest.class);
        assertEquals(authorizationRequest.getAuthorizationUri(), returned.getAuthorizationUri());
        assertEquals(authorizationRequest.getClientId(), returned.getClientId());
        assertEquals(authorizationRequest.getRedirectUri(), returned.getRedirectUri());
        assertEquals(authorizationRequest.getScopes(), returned.getScopes());
        assertEquals(authorizationRequest.getAuthorizationRequestUri(), returned.getAuthorizationRequestUri());
        assertEquals(authorizationRequest.getState(), returned.getState());
    }

}
