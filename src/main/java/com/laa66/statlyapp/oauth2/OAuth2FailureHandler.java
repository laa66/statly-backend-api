package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.jwt.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static com.laa66.statlyapp.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@AllArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        String url = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse("/");
        if (url.contains("#")) {
            String[] splitHashUrl = url.split("#");
            UriComponents fragmentUri = UriComponentsBuilder.fromUriString(splitHashUrl[1])
                    .queryParam("error", exception.getLocalizedMessage())
                    .build();
            url = UriComponentsBuilder.fromUriString(splitHashUrl[0])
                    .fragment(fragmentUri.toUriString())
                    .build()
                    .toUriString();
        } else url = UriComponentsBuilder.fromUriString(url)
                .queryParam("error", exception.getLocalizedMessage())
                .build()
                .toUriString();
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
        getRedirectStrategy().sendRedirect(request, response, url);
    }

}
