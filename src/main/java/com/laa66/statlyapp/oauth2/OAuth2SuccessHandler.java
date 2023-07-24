package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.exception.BadRequestException;
import com.laa66.statlyapp.jwt.CookieUtils;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.laa66.statlyapp.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@AllArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SpotifyTokenRepository spotifyTokenRepository;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final JwtProvider tokenProvider;
    private final URI clientAuthorizedUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2UserWrapper principal = (OAuth2UserWrapper) authentication.getPrincipal();
        spotifyTokenRepository.saveToken(principal.getUserId(), token);
        if (response.isCommitted()) return;
        String url = determineTargetUrl(request, principal.getUserId());
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, url);
    }

    protected String determineTargetUrl(HttpServletRequest request, long userId) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        redirectUri.ifPresent(uri -> {
                    URI reUri = URI.create(uri);
                    if (!clientAuthorizedUri.getHost()
                            .equalsIgnoreCase(reUri.getHost())) throw new BadRequestException("Unauthorized redirect URI");
                });

        String redirectUrl = redirectUri.orElse(getDefaultTargetUrl());
        String jwtToken = tokenProvider.createToken(userId);
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("jwt", jwtToken)
                .build()
                .toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
