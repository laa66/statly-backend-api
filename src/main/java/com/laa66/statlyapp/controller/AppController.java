package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final SpotifyAPIService spotifyApiService;

    public AppController(SpotifyAPIService spotifyAPIService, @Value("${api.react-app.url}") String reactUrl) {
        this.spotifyApiService = spotifyAPIService;
        this.reactUrl = reactUrl;
    }

    /*@GetMapping("/test")
    public void test() {
        User user = userRepository.findByEmail("lasix@gmail.com").orElseThrow();
        System.out.println(user);

        HashMap<String, Integer> artists = new HashMap<>();
        artists.put("Freeze Corleone", 1);
        UserArtist userArtist = new UserArtist(0, user.getId(), "long", LocalDate.now(), artists);
        user.addArtist(userArtist);

        HashMap<String, Integer> tracks = new HashMap<>();
        tracks.put("Hors Ligne", 1);
        UserTrack userTrack = new UserTrack(0, user.getId(), "long", tracks, LocalDate.now());
        user.addTrack(userTrack);

        HashMap<String, Double> genres = new HashMap<>();
        genres.put("rap", 50.0);
        UserGenre userGenre = new UserGenre(0, user.getId(), "long", LocalDate.now(), genres);
        user.addGenre(userGenre);

        UserMainstream userMainstream = new UserMainstream(0, user.getId(), "long", LocalDate.now(), 92.12);
        user.addMainstream(userMainstream);

        userRepository.save(user);
    }*/

    @GetMapping("/auth")
    public void user(HttpServletRequest request, HttpServletResponse response) {
        UserDTO userDTO = spotifyApiService.getCurrentUser();
        String imageUrl = userDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        String redirectUrl = reactUrl + "/callback?name=" + userDTO.getDisplayName() + "&url=" + (imageUrl.equals("none") ? "./account.png"  : imageUrl);
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    @PostMapping("/join")
    public void join(@RequestBody BetaUserDTO betaUserDTO) {
        LOGGER.info("-->> User Joined beta tests - username: " + betaUserDTO.getUsername() + ", email: " + betaUserDTO.getEmail());
    }

    @GetMapping("/top/tracks")
    public ResponseEntity<TopTracksDTO> tracks(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        TopTracksDTO topTracks = spotifyApiService.getTopTracks(email, range);
        return ResponseEntity.ok(topTracks);
    }

    @GetMapping("/top/artists")
    public ResponseEntity<TopArtistsDTO> artists(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        TopArtistsDTO topArtists = spotifyApiService.getTopArtists(email, range);
        return ResponseEntity.ok(topArtists);
    }

    @GetMapping("/top/genres")
    public ResponseEntity<TopGenresDTO> genres(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        TopGenresDTO topGenres = spotifyApiService.getTopGenres(email, range);
        return ResponseEntity.ok(topGenres);
    }

    @GetMapping("/recently")
    public ResponseEntity<RecentlyPlayedDTO> recently(@AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        RecentlyPlayedDTO recentlyPlayed = spotifyApiService.getRecentlyPlayed(email);
        return ResponseEntity.ok(recentlyPlayed);

    }

    @GetMapping("/score")
    public ResponseEntity<MainstreamScoreDTO> mainstreamScore(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        MainstreamScoreDTO mainstreamScore = spotifyApiService.getMainstreamScore(email, range);
        return ResponseEntity.ok(mainstreamScore);
    }

    @PostMapping("/playlist/create")
    public ResponseEntity<PlaylistDTO> createPlaylist(@RequestParam("range") String range, @AuthenticationPrincipal OAuth2User principal) {
        String email = (String) principal.getAttributes().get("email");
        PlaylistDTO playlist = spotifyApiService.postTopTracksPlaylist(email, range);
        return ResponseEntity.status(HttpStatus.CREATED).body(playlist);
    }
}