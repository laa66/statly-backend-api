package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Slf4j
@AllArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {

    private final SpotifyTokenRepository spotifyTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        try {
            OAuth2UserWrapper principal = (OAuth2UserWrapper) oAuth2AuthenticationToken.getPrincipal();
            spotifyTokenRepository.removeToken(principal.getUserId());
        } catch (NullPointerException e) {
            log.error("User is not authenticated", e.getCause());
        } catch (UserAuthenticationException e) {
            log.error("User external API token does not exist", e.getCause());
        }
        SecurityContextHolder.clearContext();
    }
}
