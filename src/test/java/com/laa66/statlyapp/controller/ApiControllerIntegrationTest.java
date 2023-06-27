package com.laa66.statlyapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@Import(TestSecurityConfig.class)
class ApiControllerIntegrationTest {

    @MockBean
    LibraryAnalysisService libraryAnalysisService;

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldGetTopTracksAuthenticated() throws Exception {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "short", null);
        when(spotifyAPIService.getTopTracks(1, "short")).thenReturn(tracksDTO);
        mockMvc.perform(get("/api/top/tracks").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                        .param("range", "short")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/top/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }


    @Test
    void shouldGetTopArtistsAuthenticated() throws Exception {
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist()), "short", null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        mockMvc.perform(get("/api/top/artists").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/top/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }


    @Test
    void shouldGetTopGenresAuthenticated() throws Exception {
        GenresDTO genresDTO = new GenresDTO(List.of(new Genre("rock", 2)), "short", null);
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist()), "short", null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        when(libraryAnalysisService.getTopGenres(1, "short", artistsDTO)).thenReturn(genresDTO);
        mockMvc.perform(get("/api/top/genres").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].genre", is("rock")))
                .andExpect(jsonPath("$.genres[0].score", is(2)));

        mockMvc.perform(get("/api/top/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetRecentlyPlayedAuthenticated() throws Exception {
        RecentlyPlayedDTO recentlyDTO = new RecentlyPlayedDTO("1", List.of(new PlaybackEvent()));
        when(spotifyAPIService.getRecentlyPlayed())
                .thenReturn(recentlyDTO);
        mockMvc.perform(get("/api/recently").with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/recently")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldCreatePlaylistAuthenticated() throws Exception {
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        when(spotifyAPIService.postTopTracksPlaylist(1, "short"))
                .thenReturn(playlistDTO);
        mockMvc.perform(post("/api/playlist/create")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.external_urls").exists());

        mockMvc.perform(post("/api/playlist/create").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetAllPlaylistsAuthenticated() throws Exception {
        ResponsePlaylists playlists = new ResponsePlaylists(
                null,
                1,
                List.of(new PlaylistInfo(new SpotifyURL(), "id", List.of(), "playlist", new User())));
        when(spotifyAPIService.getUserPlaylists(null))
                .thenReturn(playlists);
        mockMvc.perform(get("/api/playlist/all")
                .with(oauth2Login())
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.next").doesNotExist())
                .andExpect(jsonPath("$.total", is(1)))
                .andExpect(jsonPath("$.items").exists());

        mockMvc.perform(get("/api/playlist/all")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetLibraryAnalysisAuthenticated() throws Exception {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "long", null);
        LibraryAnalysisDTO libraryAnalysisDTO = new LibraryAnalysisDTO(
                Map.of("acousticness", 0.34, "valence", 0.55),
                List.of(new Image())
        );
        when(spotifyAPIService.getTopTracks(1L, "long"))
                .thenReturn(tracksDTO);
        when(libraryAnalysisService.getLibraryAnalysis(tracksDTO))
                .thenReturn(libraryAnalysisDTO);
        mockMvc.perform(get("/api/analysis/library")
                .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.libraryAnalysis", hasEntry("acousticness", 0.34)),
                        jsonPath("$.libraryAnalysis", hasEntry("valence", 0.55)),
                        jsonPath("$.images").exists()
                );

        mockMvc.perform(get("/api/analysis/library")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetPlaylistAnalysisAuthenticated() throws Exception {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track()), "1", "long", null);
        LibraryAnalysisDTO libraryAnalysisDTO = new LibraryAnalysisDTO(
                Map.of("acousticness", 0.34, "valence", 0.55),
                List.of(new Image())
        );
        when(spotifyAPIService.getPlaylistTracks(playlistInfo, "ES"))
                .thenReturn(tracksDTO);
        when(libraryAnalysisService.getLibraryAnalysis(tracksDTO))
                .thenReturn(libraryAnalysisDTO);
        mockMvc.perform(post("/api/analysis/playlist")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playlistInfo))
                .with(csrf())
                .with(oauth2Login().attributes(map -> map.put("country", "ES"))))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.libraryAnalysis", hasEntry("acousticness", 0.34)),
                        jsonPath("$.libraryAnalysis", hasEntry("valence", 0.55)),
                        jsonPath("$.images").exists()
                );

        mockMvc.perform(post("/api/analysis/playlist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(mapper.writeValueAsString(new PlaylistInfo())))
                .andExpect(status().isFound());

    }
}