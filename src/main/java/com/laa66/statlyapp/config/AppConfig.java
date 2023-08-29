package com.laa66.statlyapp.config;

import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.repository.impl.SpotifyTokenRepositoryImpl;
import com.laa66.statlyapp.service.*;
import com.laa66.statlyapp.service.impl.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public MailService mailService(JavaMailSender javaMailSender, @Value("${statly.api.admin-email}") String email) {
        return new MailServiceImpl(javaMailSender, email);
    }

    @Bean
    public SpotifyAPIService spotifyAPIService(@Qualifier("restTemplateInterceptor") RestTemplate restTemplate, StatsService statsService) {
        return new SpotifyAPIServiceImpl(restTemplate, statsService);
    }

    @Bean
    public MapAPIService matrixAPIService(@Value("${api.matrix.access-token}") String accessToken, @Qualifier("restTemplate") RestTemplate restTemplate) {
        return new MapboxMapAPIService(accessToken, restTemplate);
    }

    @Bean
    public LocationService locationService(MapAPIService mapAPIService, UserService userService, LibraryAnalysisService analysisService) {
        return new LocationServiceImpl(mapAPIService, userService, analysisService);
    }

    @Bean
    public SpotifyTokenService spotifyTokenService(@Qualifier("restTemplate") RestTemplate restTemplate, SpotifyTokenRepository spotifyTokenRepository) {
        return new SpotifyTokenServiceImpl(restTemplate, spotifyTokenRepository);
    }

    @Bean
    public LibraryAnalysisService libraryAnalysisService(StatsService statsService, SpotifyAPIService spotifyAPIService, SocialService socialService) {
        return new LibraryAnalysisServiceImpl(statsService, spotifyAPIService, socialService);
    }

    @Bean
    public StatsService statsService(TrackRepository trackRepository,
                                     ArtistRepository artistRepository,
                                     GenreRepository genreRepository,
                                     UserRepository userRepository) {
        return new StatsServiceImpl(trackRepository,
                artistRepository,
                genreRepository,
                userRepository);
    }

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserServiceImpl(userRepository);
    }

    @Bean
    public BetaUserService betaUserService(BetaUserRepository betaUserRepository) {
        return new BetaUserServiceImpl(betaUserRepository);
    }

    @Bean
    public SocialService socialService(UserRepository userRepository, StatsService statsService) {
        return new SocialServiceImpl(userRepository, statsService);
    }

    @Bean
    public SpotifyTokenRepository spotifyTokenRepository(CacheManager cacheManager) {
        return new SpotifyTokenRepositoryImpl(cacheManager);
    }
}
