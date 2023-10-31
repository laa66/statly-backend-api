package com.laa66.statlyapp.config;

import com.laa66.statlyapp.jwt.JwtAuthenticationFilter;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.oauth2.*;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import com.laa66.statlyapp.service.BetaUserService;
import com.laa66.statlyapp.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.net.URI;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${api.spotify.client-id}")
    private String CLIENT_ID;

    @Value("${api.spotify.client-secret}")
    private String CLIENT_SECRET;

    @Value("${api.spotify.scope}")
    private String SCOPE;

    @Value("${statly.client.url}")
    private String CLIENT_URL;

    @Value("${statly.api.admin-email}")
    private String ADMIN_EMAIL;

    @Value("${jwt.provider.secret}")
    private String STATLY_SECRET;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtProvider jwtProvider, SpotifyTokenRepository spotifyTokenRepository) {
        return new JwtAuthenticationFilter(jwtProvider, spotifyTokenRepository);
    }

    @Bean
    public JwtProvider jwtProvider() {
        return new JwtProvider(STATLY_SECRET);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler(SpotifyTokenRepository spotifyTokenRepository,
                                                     HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                                                     JwtProvider jwtProvider) {
        return new OAuth2SuccessHandler(spotifyTokenRepository, httpCookieOAuth2AuthorizationRequestRepository, jwtProvider, URI.create(CLIENT_URL));
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler(HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository) {
        return new OAuth2FailureHandler(httpCookieOAuth2AuthorizationRequestRepository);
    }

    @Bean
    public OAuth2LogoutHandler oAuth2LogoutHandler(SpotifyTokenRepository spotifyTokenRepository) {
        return new OAuth2LogoutHandler(spotifyTokenRepository);
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(UserService userService, BetaUserService betaUserService) {
        return new CustomOAuth2UserService(betaUserService, userService);
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.addAllowedOrigin(CLIENT_URL);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                                   OAuth2UserService<OAuth2UserRequest, OAuth2User> userService,
                                                   HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
                                                   OAuth2SuccessHandler oAuth2SuccessHandler,
                                                   OAuth2FailureHandler oAuth2FailureHandler,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   OAuth2LogoutHandler oAuth2LogoutHandler) throws Exception {
        httpSecurity
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests()
                    .requestMatchers(HttpMethod.GET, "/beta/join")
                    .permitAll()
                    .requestMatchers("/beta/all", "/beta/delete", "/beta/notification")
                    .access((authentication, object) ->
                        new AuthorizationDecision(((OAuth2User) authentication.get().getPrincipal())
                                .getAttributes()
                                .get("email")
                                .equals(ADMIN_EMAIL)))
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login(login -> login
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/oauth2/authorize")
                                .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository))
                        .userInfoEndpoint(infoEndpoint -> infoEndpoint
                                .userService(userService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler))
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .permitAll(false)
                        .clearAuthentication(false)
                        .addLogoutHandler(oAuth2LogoutHandler)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)))
                .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(spotifyClientRegistration());
    }

    private ClientRegistration spotifyClientRegistration() {
        return ClientRegistration.withRegistrationId("spotify")
                .clientName("spotify")
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .scope(SCOPE)
                .authorizationUri("https://accounts.spotify.com/authorize?show_dialog=true")
                .tokenUri("https://accounts.spotify.com/api/token")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .userInfoUri("https://api.spotify.com/v1/me")
                .userNameAttributeName("display_name")
                .build();
    }
}
