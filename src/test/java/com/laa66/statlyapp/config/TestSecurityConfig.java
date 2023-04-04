package com.laa66.statlyapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {
    @Value("${api.spotify.client-id}")
    private String CLIENT_ID;

    @Value("${api.spotify.client-secret}")
    private String CLIENT_SECRET;

    @Value("${api.spotify.scope}")
    private String SCOPE;

    @Value("${dev.react-app.url}")
    private String DEV_REACT_URL;

    @Bean
    public FilterRegistrationBean<CorsFilter> customCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedMethods(Collections.singletonList("*"));
        config.setAllowedHeaders(Collections.singletonList("*"));
        config.addAllowedOrigin(DEV_REACT_URL);
        config.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        XorCsrfTokenRequestAttributeHandler delegate = new XorCsrfTokenRequestAttributeHandler();
        delegate.setCsrfRequestAttributeName(null);
        CsrfTokenRequestHandler requestHandler = delegate::handle;
        httpSecurity.csrf((csrf) -> csrf
                        .csrfTokenRepository(tokenRepository)
                        .csrfTokenRequestHandler(requestHandler))
                .authorizeHttpRequests()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
                .and()
                .oauth2Login();
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
