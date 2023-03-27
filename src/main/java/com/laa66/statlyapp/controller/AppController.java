package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public void user(HttpServletRequest request, HttpServletResponse response) {
        UserIdDTO userIdDTO = spotifyApiService.getCurrentUser();
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