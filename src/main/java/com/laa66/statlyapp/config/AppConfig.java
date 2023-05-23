package com.laa66.statlyapp.config;

import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.*;
import com.laa66.statlyapp.task.CacheTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.server.CookieSameSiteSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Bean
    public MailService mailService(JavaMailSender javaMailSender, @Value("${statly.api.admin-email}") String email) {
        return new MailServiceImpl(javaMailSender, email);
    }

    @Bean
    public SpotifyAPIService spotifyAPIService(@Qualifier("restTemplateInterceptor") RestTemplate restTemplate, StatsService statsService) {
        return new SpotifyAPIServiceImpl(restTemplate, statsService);
    }

    @Bean
    public SpotifyTokenService spotifyTokenService(@Qualifier("restTemplate") RestTemplate restTemplate) {
        return new SpotifyTokenServiceImpl(restTemplate);
    }

    @Bean
    public StatsService statsService(TrackRepository trackRepository,
                                     ArtistRepository artistRepository,
                                     GenreRepository genreRepository,
                                     MainstreamRepository mainstreamRepository) {
        return new StatsServiceImpl(trackRepository, artistRepository, genreRepository, mainstreamRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository, BetaUserRepository betaUserRepository) {
        return new UserServiceImpl(userRepository, betaUserRepository);
    }

    @Bean
    public CookieSameSiteSupplier cookieSameSiteSupplier(){
        return CookieSameSiteSupplier.ofNone().whenHasName("XSRF-TOKEN");
    }
}
