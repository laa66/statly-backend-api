package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.exchange.*;
import com.laa66.statlyapp.service.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AppController {

    @Autowired
    private SpotifyApiService spotifyApiService;

    @GetMapping("/top/tracks")
    public SpotifyResponseTopTracks tracks(@RequestParam("range") String range) {
        SpotifyResponseTopTracks tracks;
        switch (range) {
            case "short" -> tracks = spotifyApiService.getTopTracks(SpotifyAPI.TOP_TRACKS_SHORT);
            case "medium" -> tracks = spotifyApiService.getTopTracks(SpotifyAPI.TOP_TRACKS_MEDIUM);
            case "long" -> tracks = spotifyApiService.getTopTracks(SpotifyAPI.TOP_TRACKS_LONG);
            default -> throw new RuntimeException();
        }
        return tracks;
    }

    @GetMapping("/top/artists")
    public SpotifyResponseTopArtists artists(@RequestParam("range") String range) {
        SpotifyResponseTopArtists artists;
        switch (range) {
            case "short" -> artists = spotifyApiService.getTopArtists(SpotifyAPI.TOP_ARTISTS_SHORT);
            case "medium" -> artists = spotifyApiService.getTopArtists(SpotifyAPI.TOP_ARTISTS_MEDIUM);
            case "long" -> artists = spotifyApiService.getTopArtists(SpotifyAPI.TOP_ARTISTS_LONG);
            default -> throw new RuntimeException();
        }
        return artists;
    }

    @GetMapping("/top/genres")
    public SpotifyResponseTopGenres genres(@RequestParam("range") String range) {
        SpotifyResponseTopGenres genres;
        switch (range) {
            case "short" -> genres = spotifyApiService.getTopGenres(SpotifyAPI.TOP_ARTISTS_SHORT);
            case "medium" -> genres = spotifyApiService.getTopGenres(SpotifyAPI.TOP_ARTISTS_MEDIUM);
            case "long" -> genres = spotifyApiService.getTopGenres(SpotifyAPI.TOP_ARTISTS_LONG);
            default -> throw new RuntimeException();
        }
        return genres;
    }

    @GetMapping("/recently")
    public SpotifyResponseRecentlyPlayed recently() {
        return spotifyApiService.getRecentlyPlayed();
    }

    @GetMapping("/score")
    public SpotifyResponseMainstreamScore mainstreamScore(@RequestParam("range") String range) {
        SpotifyResponseMainstreamScore score;
        switch (range) {
            case "short" -> score = spotifyApiService.getMainstreamScore(SpotifyAPI.TOP_ARTISTS_SHORT);
            case "medium" -> score = spotifyApiService.getMainstreamScore(SpotifyAPI.TOP_ARTISTS_MEDIUM);
            case "long" -> score = spotifyApiService.getMainstreamScore(SpotifyAPI.TOP_ARTISTS_LONG);
            default -> throw new RuntimeException();
        }
        return score;
    }

    @PostMapping("/playlist/create")
    public String createPlaylist(@RequestParam("range") String range) {
        String response;
        switch (range) {
            case "short" -> response = spotifyApiService.postTopTracksPlaylist(SpotifyAPI.TOP_TRACKS_SHORT);
            case "medium" -> response = spotifyApiService.postTopTracksPlaylist(SpotifyAPI.TOP_TRACKS_MEDIUM);
            case "long" -> response = spotifyApiService.postTopTracksPlaylist(SpotifyAPI.TOP_TRACKS_LONG);
            default -> throw new RuntimeException();
        }
        return response;
    }

}
