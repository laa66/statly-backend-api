package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.jwt.CookieUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import static com.laa66.statlyapp.jwt.CookieUtils.serialize;
import static org.junit.jupiter.api.Assertions.*;

public class HttpCookieOAuth2AuthorizationRequestRepositoryUnitTest {

    HttpCookieOAuth2AuthorizationRequestRepository requestRepository = new HttpCookieOAuth2AuthorizationRequestRepository();

    OAuth2AuthorizationRequest authorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
            .authorizationUri("uri")
            .clientId("client")
            .redirectUri("redirect_uri")
            .scope("USER")
            .authorizationRequestUri("request_uri")
            .state("state")
            .build();

    @Test
    void shouldLoadAuthorizationRequest() {
        String value = serialize(authorizationRequest);
        Cookie cookie = new Cookie("oauth2_auth_request", value);

        MockHttpServletRequest request1 = new MockHttpServletRequest();
        request1.setCookies(cookie);
        OAuth2AuthorizationRequest returned1 = requestRepository.loadAuthorizationRequest(request1);
        assertEquals(authorizationRequest.getState(), returned1.getState());
        assertEquals(authorizationRequest.getClientId(), returned1.getClientId());
        assertEquals(authorizationRequest.getRedirectUri(), returned1.getRedirectUri());

        MockHttpServletRequest request2 = new MockHttpServletRequest();
        OAuth2AuthorizationRequest returned2 = requestRepository.loadAuthorizationRequest(request2);
        assertNull(returned2);
    }

    @Test
    void shouldSaveAuthorizationRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("redirect_uri", "uri");
        MockHttpServletResponse response = new MockHttpServletResponse();
        requestRepository.saveAuthorizationRequest(authorizationRequest, request, response);
        OAuth2AuthorizationRequest deserializedCookieAuth = CookieUtils.deserialize(response.getCookies()[0], OAuth2AuthorizationRequest.class);
        assertEquals(2, response.getCookies().length);
        assertEquals(180, response.getCookies()[0].getMaxAge());
        assertEquals(authorizationRequest.getState(), deserializedCookieAuth.getState());
        assertEquals(authorizationRequest.getClientId(), deserializedCookieAuth.getClientId());
        assertEquals(authorizationRequest.getRedirectUri(), deserializedCookieAuth.getRedirectUri());
        assertEquals(180, response.getCookies()[1].getMaxAge());
        assertEquals("uri", response.getCookies()[1].getValue());
    }

    @Test
    void shouldSaveAuthorizationRequestNullAuthorizationRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("redirect_uri", "uri"),
                new Cookie("oauth2_auth_request", CookieUtils.serialize(authorizationRequest)));
        MockHttpServletResponse response = new MockHttpServletResponse();
        requestRepository.saveAuthorizationRequest(null, request, response);
        assertEquals(2, response.getCookies().length);
        assertEquals("", response.getCookies()[0].getValue());
        assertEquals(0, response.getCookies()[0].getMaxAge());
        assertEquals("", response.getCookies()[1].getValue());
        assertEquals(0, response.getCookies()[1].getMaxAge());
    }

    @Test
    void shouldRemoveAuthorizationRequestCookies() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie("redirect_uri", "uri"),
                new Cookie("oauth2_auth_request", CookieUtils.serialize(authorizationRequest)));
        MockHttpServletResponse response = new MockHttpServletResponse();
        requestRepository.saveAuthorizationRequest(null, request, response);
        assertEquals(2, response.getCookies().length);
        assertEquals("", response.getCookies()[0].getValue());
        assertEquals(0, response.getCookies()[0].getMaxAge());
        assertEquals("", response.getCookies()[1].getValue());
        assertEquals(0, response.getCookies()[1].getMaxAge());
    }
}
