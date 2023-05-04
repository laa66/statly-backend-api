package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final SpotifyAPIService spotifyApiService;

    public ApiController(SpotifyAPIService spotifyAPIService) {
        this.spotifyApiService = spotifyAPIService;
    }

    @GetMapping("/top/tracks")
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopTracksDTO dto = spotifyApiService.getTopTracks(userId, range);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopArtistsDTO dto = spotifyApiService.getTopArtists(userId, range);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopGenresDTO dto = spotifyApiService.getTopGenres(userId, range);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently() {
        RecentlyPlayedDTO dto = spotifyApiService.getRecentlyPlayed();
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        MainstreamScoreDTO dto = spotifyApiService.getMainstreamScore(userId, range);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        PlaylistDTO playlist = spotifyApiService.postTopTracksPlaylist(userId, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }
}