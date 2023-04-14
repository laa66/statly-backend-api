package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    @Value("${api.react-app.url}")
    private String REACT_URL;

    @Autowired
    private SpotifyAPIService spotifyApiService;

    @GetMapping("/auth")
    public void user(HttpServletRequest request, HttpServletResponse response) {
        UserIdDTO userIdDTO = spotifyApiService.getCurrentUser();
        String imageUrl = userIdDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        String redirectUrl = REACT_URL + "/callback?name=" + StringUtils.stripAccents(userIdDTO.getDisplayName()) + "&url=" + (imageUrl.equals("none") ? "./account.png"  : imageUrl);
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    @PostMapping("/join")
    public void join(@RequestBody BetaUserDTO betaUserDTO) {
        LOGGER.info("-->> User Joined beta tests - username: " + betaUserDTO.getUsername() + ", email: " + betaUserDTO.getEmail());
    }

    @GetMapping("/top/tracks")
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        TopTracksDTO topTracks = spotifyApiService.getTopTracks(principal.getName(), url);
        return ResponseEntity.ok(topTracks);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        TopArtistsDTO topArtists = spotifyApiService.getTopArtists(principal.getName(), url);
        return ResponseEntity.ok(topArtists);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        TopGenresDTO topGenres = spotifyApiService.getTopGenres(principal.getName(), url);
        return ResponseEntity.ok(topGenres);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently(Principal principal) {
        RecentlyPlayedDTO recentlyPlayed = spotifyApiService.getRecentlyPlayed(principal.getName());
        return ResponseEntity.ok(recentlyPlayed);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        MainstreamScoreDTO mainstreamScore = spotifyApiService.getMainstreamScore(principal.getName(), url);
        return ResponseEntity.ok(mainstreamScore);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        PlaylistDTO playlist = spotifyApiService.postTopTracksPlaylist(principal.getName(), url);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }
}