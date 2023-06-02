package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final SpotifyAPIService spotifyApiService;
    private final LibraryAnalysisService libraryAnalysisService;

    @GetMapping("/top/tracks")
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopTracksDTO tracksDTO = spotifyApiService.getTopTracks(userId, range);
        return ResponseEntity.ok(tracksDTO);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        return ResponseEntity.ok(artistsDTO);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        TopGenresDTO genresDTO = libraryAnalysisService.getTopGenres(userId, range, artistsDTO);
        return ResponseEntity.ok(genresDTO);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently() {
        RecentlyPlayedDTO recentlyPlayedDTO = spotifyApiService.getRecentlyPlayed();
        return ResponseEntity.ok(recentlyPlayedDTO);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        MainstreamScoreDTO mainstreamScoreDTO = spotifyApiService.getMainstreamScore(userId, range);
        return ResponseEntity.ok(mainstreamScoreDTO);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        PlaylistDTO playlistDTO = spotifyApiService.postTopTracksPlaylist(userId, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistDTO);
    }
}