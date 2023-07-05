package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.PlaylistInfo;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final SpotifyAPIService spotifyApiService;
    private final LibraryAnalysisService libraryAnalysisService;

    @GetMapping("/top/tracks")
    public ResponseEntity<TracksDTO> tracks(@RequestParam String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TracksDTO tracksDTO = spotifyApiService.getTopTracks(userId, range);
        return ResponseEntity.ok(tracksDTO);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<ArtistsDTO> artists(@RequestParam String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        ArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        return ResponseEntity.ok(artistsDTO);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<GenresDTO> genres(@RequestParam String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        ArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        GenresDTO genresDTO = libraryAnalysisService.getTopGenres(userId, range, artistsDTO);
        return ResponseEntity.ok(genresDTO);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently() {
        RecentlyPlayedDTO recentlyPlayedDTO = spotifyApiService.getRecentlyPlayed();
        return ResponseEntity.ok(recentlyPlayedDTO);

    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        PlaylistDTO playlistDTO = spotifyApiService.postTopTracksPlaylist(userId, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistDTO);
    }

    @GetMapping("/playlist/all")
    public ResponseEntity<ResponsePlaylists> getPlaylists() {
        ResponsePlaylists playlist = spotifyApiService.getUserPlaylists(null);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/analysis/library")
    public ResponseEntity<LibraryAnalysisDTO> libraryAnalysis(@AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TracksDTO tracksDTO = spotifyApiService.getTopTracks(userId, "long");
        LibraryAnalysisDTO libraryAnalysisDTO = libraryAnalysisService.getLibraryAnalysis(tracksDTO, userId);
        return ResponseEntity.ok(libraryAnalysisDTO);
    }

    @PostMapping("/analysis/playlist")
    public ResponseEntity<LibraryAnalysisDTO> playlistAnalysis(@AuthenticationPrincipal OAuth2User principal, @RequestBody PlaylistInfo playlistInfo) {
        String country = (String) principal.getAttributes().get("country");
        TracksDTO tracksDTO = spotifyApiService.getPlaylistTracks(playlistInfo, country);
        LibraryAnalysisDTO libraryAnalysisDTO = libraryAnalysisService.getLibraryAnalysis(tracksDTO, null);
        return ResponseEntity.ok(libraryAnalysisDTO);
    }

    @GetMapping("/analysis/match")
    public ResponseEntity<Map<String, Double>> matchUsers(@AuthenticationPrincipal OAuth2User principal, @RequestParam("user_id") long matchUserId) {
        long userId = (long) principal.getAttributes().get("userId");
        Map<String, Double> usersMatching = libraryAnalysisService.getUsersMatching(userId, matchUserId);
        return ResponseEntity.ok(usersMatching);
    }
}