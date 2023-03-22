package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.security.Principal;

@RestController
@RequestMapping("/api")
public class AppController {

    @Value("${dev.react-app.url}")
    private String REACT_URL;

    @Autowired
    private SpotifyAPIService spotifyApiService;

    @GetMapping("/auth")
    public ResponseEntity<Void> user() {
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(REACT_URL + "/dashboard")).build();
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

    @PostMapping("/playlist/create")
    public String createPlaylist(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.postTopTracksPlaylist(principal.getName(), url);
    }
}