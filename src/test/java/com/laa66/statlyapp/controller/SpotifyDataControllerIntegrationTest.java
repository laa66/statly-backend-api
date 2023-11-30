package com.laa66.statlyapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.spotify.response.ResponsePlaylists;
import com.laa66.statlyapp.model.spotify.*;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpotifyDataController.class)
@Import(TestSecurityConfig.class)
class SpotifyDataControllerIntegrationTest {

    @MockBean
    SpotifyTokenRepository spotifyTokenRepository;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    LibraryAnalysisService libraryAnalysisService;

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    OAuth2AuthenticationToken token;

    @BeforeEach
    void setup() {
        token = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                        Collections.emptyList(), Map.of("display_name", "name", "userId", 1L, "country", "ES"), "display_name"
                )), Collections.emptyList(), "client");
        when(jwtProvider.validateToken("token")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token")).thenReturn(1L);
        when(spotifyTokenRepository.getToken(1L)).thenReturn(token);
    }

    @Test
    void shouldGetTopTracks() throws Exception {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "short", null, null);
        when(spotifyAPIService.getTopTracks(1, "short")).thenReturn(tracksDTO);
        mockMvc.perform(get("/api/spotify/tracks/top")
                        .header("Authorization", "Bearer token")
                        .param("range", "short")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/spotify/tracks/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }


    @Test
    void shouldGetTopArtists() throws Exception {
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist()), "short", null, null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        mockMvc.perform(get("/api/spotify/artists/top")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/spotify/artists/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }


    @Test
    void shouldGetTopGenres() throws Exception {
        GenresDTO genresDTO = new GenresDTO(List.of(new Genre("rock", 2)), "short", null, null);
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist()), "short", null, null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        when(libraryAnalysisService.getTopGenres(1, "short", artistsDTO)).thenReturn(genresDTO);
        mockMvc.perform(get("/api/spotify/genres/top")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].genre", is("rock")))
                .andExpect(jsonPath("$.genres[0].score", is(2)));

        mockMvc.perform(get("/api/spotify/genres/top")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetRecentlyPlayed() throws Exception {
        RecentlyPlayedDTO recentlyDTO = new RecentlyPlayedDTO("1", List.of(new PlaybackEvent()));
        when(spotifyAPIService.getRecentlyPlayed())
                .thenReturn(recentlyDTO);
        mockMvc.perform(get("/api/spotify/tracks/history")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/spotify/tracks/history")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldCreatePlaylist() throws Exception {
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        when(spotifyAPIService.postTopTracksPlaylist(1, "short"))
                .thenReturn(playlistDTO);
        mockMvc.perform(post("/api/spotify/playlist/create")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.external_urls").exists());

        mockMvc.perform(post("/api/spotify/playlist/create").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetAllPlaylists() throws Exception {
        ResponsePlaylists playlists = new ResponsePlaylists(
                null,
                1,
                List.of(new PlaylistInfo(new SpotifyURL(), "id", List.of(), "playlist",
                        UserDTO.builder()
                                .id("1")
                                .uri("uri")
                                .email("mail")
                                .name("username")
                                .images(List.of())
                                .points(0)
                                .build()
                )));
        when(spotifyAPIService.getUserPlaylists(null))
                .thenReturn(playlists);
        mockMvc.perform(get("/api/spotify/playlist/all")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.next").doesNotExist())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/spotify/playlist/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetLibraryAnalysis() throws Exception {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "long", null, null);
        AnalysisDTO analysisDTO = new AnalysisDTO(
                Map.of("acousticness", 0.34, "valence", 0.55),
                List.of(new Image())
        );
        when(spotifyAPIService.getTopTracks(1L, "long"))
                .thenReturn(tracksDTO);
        when(libraryAnalysisService.getTracksAnalysis(tracksDTO, 1L))
                .thenReturn(analysisDTO);
        mockMvc.perform(get("/api/spotify/analysis/library")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.analysis", hasEntry("acousticness", 0.34)),
                        jsonPath("$.analysis", hasEntry("valence", 0.55)),
                        jsonPath("$.images").exists()
                );

        mockMvc.perform(get("/api/spotify/analysis/library")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetPlaylistAnalysis() throws Exception {
        PlaylistInfo playlistInfo = new PlaylistInfo(new SpotifyURL(), "id", List.of(), "name", null);
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "long", null, null);
        AnalysisDTO analysisDTO = new AnalysisDTO(
                Map.of("acousticness", 0.34, "valence", 0.55),
                List.of(new Image())
        );
        when(spotifyAPIService.getPlaylistTracks(playlistInfo, "ES"))
                .thenReturn(tracksDTO);
        when(libraryAnalysisService.getTracksAnalysis(tracksDTO, null))
                .thenReturn(analysisDTO);
        mockMvc.perform(post("/api/spotify/analysis/playlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(playlistInfo))
                        .header("Authorization", "Bearer token"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.analysis", hasEntry("acousticness", 0.34)),
                        jsonPath("$.analysis", hasEntry("valence", 0.55)),
                        jsonPath("$.images").exists()
                );

        mockMvc.perform(post("/api/spotify/analysis/playlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null))
                        .with(csrf())
                        .with(oauth2Login().attributes(map -> map.put("country", "ES"))))
                .andExpect(status().isBadRequest());


        mockMvc.perform(post("/api/spotify/analysis/playlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(mapper.writeValueAsString(new PlaylistInfo(
                                new SpotifyURL(), "id", List.of(), "name", null))))
                .andExpect(status().isFound());

    }

    @Test
    void shouldMatchUsers() throws Exception {
        Map<String, Double> usersMatch = Map.of("track", 22., "artist", 30., "genre", 31., "overall", 40.);
        when(libraryAnalysisService.getUsersMatching(1,2)).thenReturn(usersMatch);
        mockMvc.perform(get("/api/spotify/analysis/match?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasEntry("track", 22.)),
                        jsonPath("$", hasEntry("artist", 30.)),
                        jsonPath("$", hasEntry("genre", 31.)),
                        jsonPath("$", hasEntry("overall", 40.))
                );

        when(libraryAnalysisService.getUsersMatching(1,3))
                .thenReturn(Map.of(
                "track", 0., "artist", 0., "genre", 0., "overall", 0.
        ));
        mockMvc.perform(get("/api/spotify/analysis/match?user_id=3")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasEntry("track", 0.)),
                        jsonPath("$", hasEntry("artist", 0.)),
                        jsonPath("$", hasEntry("genre", 0.)),
                        jsonPath("$", hasEntry("overall", 0.))
                );
    }

    @Test
    void shouldCreatePlaylistBattle() throws Exception {
        BattleResultDTO battleResultDTO = new BattleResultDTO(
                null,
                null,
                new Battler(1L, new AnalysisDTO(Map.of(), List.of())),
                new Battler(2L, new AnalysisDTO(Map.of(), List.of())),
                50.
        );
        when(libraryAnalysisService.createPlaylistBattle(anyLong(), anyLong(), any(), any()))
                .thenReturn(battleResultDTO);
        mockMvc.perform(post("/api/spotify/analysis/battle?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(new BattleDTO(
                                        new PlaylistInfo(null, "id1", List.of(), "name1", null),
                                        new PlaylistInfo(null, "id2", List.of(), "name2", null)))))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.result", is(50.)),
                        jsonPath("$.winner.id", is(1)),
                        jsonPath("$.loser.id", is(2)));


        mockMvc.perform(post("/api/spotify/analysis/battle?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(null)))
                .andExpect(status().isBadRequest());

    }
}