package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        TopTracksDTO topTracks = spotifyApiService.getTopTracks(principal.getName(), url);
        return new ResponseEntity<>(topTracks, topTracks == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        TopArtistsDTO topArtists = spotifyApiService.getTopArtists(principal.getName(), url);
        return new ResponseEntity<>(topArtists, topArtists == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        TopGenresDTO topGenres = spotifyApiService.getTopGenres(principal.getName(), url);
        return new ResponseEntity<>(topGenres, topGenres == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently(Principal principal) {
        RecentlyPlayedDTO recentlyPlayed = spotifyApiService.getRecentlyPlayed(principal.getName());
        return new ResponseEntity<>(recentlyPlayed, recentlyPlayed == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        MainstreamScoreDTO mainstreamScore = spotifyApiService.getMainstreamScore(principal.getName(), url);
        return new ResponseEntity<>(mainstreamScore, mainstreamScore == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<String> createPlaylist(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        String snapshot = spotifyApiService.postTopTracksPlaylist(principal.getName(), url);
        return new ResponseEntity<>(snapshot, snapshot == null ? HttpStatus.CONFLICT : HttpStatus.CREATED);
    }
}