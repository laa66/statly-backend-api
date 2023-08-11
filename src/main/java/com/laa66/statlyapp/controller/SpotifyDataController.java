package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.model.spotify.PlaylistInfo;
import com.laa66.statlyapp.model.spotify.response.ResponsePlaylists;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/spotify")
@RequiredArgsConstructor
public class SpotifyDataController {

    private final SpotifyAPIService spotifyApiService;
    private final LibraryAnalysisService libraryAnalysisService;

    @GetMapping("/tracks/top")
    public ResponseEntity<TracksDTO> tracks(@RequestParam String range, @AuthenticationPrincipal OAuth2UserWrapper principal) {
        long userId = principal.getUserId();
        TracksDTO tracksDTO = spotifyApiService.getTopTracks(userId, range);
        return ResponseEntity.ok(tracksDTO);
    }

    @GetMapping("/artists/top")
    public ResponseEntity<ArtistsDTO> artists(@RequestParam String range, @AuthenticationPrincipal OAuth2UserWrapper principal) {
        long userId = principal.getUserId();
        ArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        return ResponseEntity.ok(artistsDTO);
    }

    @GetMapping("/genres/top")
    public ResponseEntity<GenresDTO> genres(@RequestParam String range, @AuthenticationPrincipal OAuth2UserWrapper principal) {
        long userId = principal.getUserId();
        ArtistsDTO artistsDTO = spotifyApiService.getTopArtists(userId, range);
        GenresDTO genresDTO = libraryAnalysisService.getTopGenres(userId, range, artistsDTO);
        return ResponseEntity.ok(genresDTO);
    }

    @GetMapping("/tracks/history")
    public ResponseEntity<RecentlyPlayedDTO> recently() {
        RecentlyPlayedDTO recentlyPlayedDTO = spotifyApiService.getRecentlyPlayed();
        return ResponseEntity.ok(recentlyPlayedDTO);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam String range, @AuthenticationPrincipal OAuth2UserWrapper principal) {
        long userId = principal.getUserId();
        PlaylistDTO playlistDTO = spotifyApiService.postTopTracksPlaylist(userId, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlistDTO);
    }

    @GetMapping("/playlist/all")
    public ResponseEntity<ResponsePlaylists> getPlaylists(@RequestParam(value = "external_id", required = false) String externalUserId) {
        ResponsePlaylists playlist = spotifyApiService.getUserPlaylists(externalUserId);
        return ResponseEntity.ok(playlist);
    }

    @GetMapping("/analysis/library")
    public ResponseEntity<AnalysisDTO> libraryAnalysis(@AuthenticationPrincipal OAuth2UserWrapper principal) {
        long userId = principal.getUserId();
        TracksDTO tracksDTO = spotifyApiService.getTopTracks(userId, "long");
        AnalysisDTO analysisDTO = libraryAnalysisService.getTracksAnalysis(tracksDTO, userId);
        return ResponseEntity.ok(analysisDTO);
    }

    @PostMapping("/analysis/playlist")
    public ResponseEntity<AnalysisDTO> playlistAnalysis(@AuthenticationPrincipal OAuth2UserWrapper principal, @Valid @RequestBody PlaylistInfo playlistInfo) {
        String country = principal.getCountry();
        TracksDTO tracksDTO = spotifyApiService.getPlaylistTracks(playlistInfo, country);
        AnalysisDTO analysisDTO = libraryAnalysisService.getTracksAnalysis(tracksDTO, null);
        return ResponseEntity.ok(analysisDTO);
    }

    @GetMapping("/analysis/match")
    public ResponseEntity<Map<String, Double>> matchUsers(@AuthenticationPrincipal OAuth2UserWrapper principal, @RequestParam("user_id") long matchUserId) {
        long userId = principal.getUserId();
        Map<String, Double> usersMatching = libraryAnalysisService.getUsersMatching(userId, matchUserId);
        return ResponseEntity.ok(usersMatching);
    }

    @PostMapping("/analysis/battle")
    public ResponseEntity<BattleResultDTO> createPlaylistBattle(@AuthenticationPrincipal OAuth2UserWrapper principal,
                                                              @RequestParam("user_id") long battleUserId,
                                                              @Valid @RequestBody BattleDTO battleDTO) {
        long userId = principal.getUserId();
        String country = principal.getCountry();
        TracksDTO tracks = spotifyApiService.getPlaylistTracks(battleDTO.getPlaylist(), country);
        TracksDTO tracksBattle = spotifyApiService.getPlaylistTracks(battleDTO.getPlaylistBattle(), country);
        BattleResultDTO battleResultDTO = libraryAnalysisService.createPlaylistBattle(userId, battleUserId, tracks, tracksBattle);
        return ResponseEntity.ok(battleResultDTO);
    }
}