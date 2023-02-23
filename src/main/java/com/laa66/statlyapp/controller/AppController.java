package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class AppController {

    @Autowired
    private SpotifyApiService spotifyApiService;

    @GetMapping("/topTracks")
    public SpotifyResponseTopTracks tracks() {
        return spotifyApiService.getTopTracks(SpotifyAPI.TOP_TRACKS_SHORT);
    }

    @GetMapping("/topArtists")
    public SpotifyResponseTopArtists artists() {
        return spotifyApiService.getTopArtists(SpotifyAPI.TOP_ARTISTS_SHORT);
    }

    @GetMapping("/topGenres")
    public SpotifyResponseTopGenres genres() {
        return spotifyApiService.getTopGenres(SpotifyAPI.TOP_ARTISTS_SHORT);
    }

    @GetMapping("/recently")
    public SpotifyResponseRecentlyPlayed recently() {
        return spotifyApiService.getRecentlyPlayed();
    }

}
