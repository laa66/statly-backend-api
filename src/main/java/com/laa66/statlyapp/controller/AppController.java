package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.exchange.*;
import com.laa66.statlyapp.service.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private SpotifyApiService spotifyApiService;

    @GetMapping("/top/tracks")
    public SpotifyResponseTopTracks tracks(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.getTopTracks(principal.getName(), url);
    }


    @GetMapping("/top/artists")
    public SpotifyResponseTopArtists artists(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        return spotifyApiService.getTopArtists(principal.getName(), url);
    }

    @GetMapping("/top/genres")
    public SpotifyResponseTopGenres genres(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        return spotifyApiService.getTopGenres(principal.getName(), url);
    }

    @GetMapping("/recently")
    public SpotifyResponseRecentlyPlayed recently(Principal principal) {
        return spotifyApiService.getRecentlyPlayed(principal.getName());
    }

    @GetMapping("/score")
    public SpotifyResponseMainstreamScore mainstreamScore(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_ARTISTS + range + "_term";
        return spotifyApiService.getMainstreamScore(principal.getName(), url);
    }

    @GetMapping("/playlist/create")
    public String createPlaylist(@RequestParam("range") String range, Principal principal) {
        String url = SpotifyAPI.TOP_TRACKS + range + "_term";
        return spotifyApiService.postTopTracksPlaylist(principal.getName(), url);
    }

}
