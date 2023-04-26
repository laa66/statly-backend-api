package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class AppController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    private final String reactUrl;
    private final UserService userService;
    private final SpotifyAPIService spotifyApiService;

    public AppController(UserService userService, SpotifyAPIService spotifyAPIService, @Value("${api.react-app.url}") String reactUrl) {
        this.userService = userService;
        this.spotifyApiService = spotifyAPIService;
        this.reactUrl = reactUrl;
    }

    @GetMapping("/auth")
    public void authenticate(HttpServletRequest request, HttpServletResponse response) {
        UserDTO userDTO = spotifyApiService.getCurrentUser();
        String imageUrl = userDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        String redirectUrl = reactUrl + "/callback?name=" + StringUtils.stripAccents(userDTO.getDisplayName()) + "&url=" + (imageUrl.equals("none") ? "./account.png"  : imageUrl);
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    @PostMapping("/join")
    public void join(@RequestBody BetaUserDTO betaUserDTO) {
        LOGGER.info("-->> User Joined beta tests - username: " + betaUserDTO.getUsername() + ", email: " + betaUserDTO.getEmail());
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal OAuth2User principal) {
        userService.deleteUser((long) principal.getAttributes().get("userId"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top/tracks")
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopTracksDTO dto = spotifyApiService.getTopTracks(userId, range);
        TopTracksDTO comparedDto = userService.compareTracks(userId, dto);
        return ResponseEntity.ok(comparedDto);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopArtistsDTO dto = spotifyApiService.getTopArtists(userId, range);
        TopArtistsDTO comparedDto = userService.compareArtists(userId, dto);
        return ResponseEntity.ok(comparedDto);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        TopGenresDTO dto = spotifyApiService.getTopGenres(userId, range);
        TopGenresDTO comparedDto = userService.compareGenres(userId, dto);
        return ResponseEntity.ok(comparedDto);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently() {
        RecentlyPlayedDTO recentlyPlayed = spotifyApiService.getRecentlyPlayed();
        return ResponseEntity.ok(recentlyPlayed);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        MainstreamScoreDTO dto = spotifyApiService.getMainstreamScore(userId, range);
        MainstreamScoreDTO comparedDto = userService.compareMainstream(userId, dto);
        return ResponseEntity.ok(comparedDto);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        PlaylistDTO playlist = spotifyApiService.postTopTracksPlaylist(userId, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }
}