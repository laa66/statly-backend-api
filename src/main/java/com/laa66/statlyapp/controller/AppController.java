package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AppController {

    @Value("${dev.react-app.url}")
    private String REACT_URL;

    @Autowired
    private SpotifyAPIService spotifyApiService;

    @GetMapping("/auth")
    public void user(HttpServletRequest request, HttpServletResponse response) {UserIdDTO userIdDTO = spotifyApiService.getCurrentUser();
        response.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        String imageUrl = userIdDTO.getImages().size() > 0 ? userIdDTO.getImages().get(0).getUrl() : "none";
        response.setHeader("location", REACT_URL + "/callback?name=" + userIdDTO.getDisplayName() + "&url=" + imageUrl);
    }

    @GetMapping("/top/tracks")
    public TopTracksDTO tracks(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.getTopTracks(principal.getName(), url);
    }


    @GetMapping("/top/artists")
    public TopArtistsDTO artists(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        return spotifyApiService.getTopArtists(principal.getName(), url);
    }

    @GetMapping("/top/genres")
    public TopGenresDTO genres(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        return spotifyApiService.getTopGenres(principal.getName(), url);
    }

    @GetMapping("/recently")
    public RecentlyPlayedDTO recently(Principal principal) {
        return spotifyApiService.getRecentlyPlayed(principal.getName());
    }

    @GetMapping("/score")
    public MainstreamScoreDTO mainstreamScore(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.getMainstreamScore(principal.getName(), url);
    }

    @CrossOrigin("*")
    @PostMapping("/playlist/create")
    public String createPlaylist(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.postTopTracksPlaylist(principal.getName(), url);
    }
}