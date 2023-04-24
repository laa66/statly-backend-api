package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
@ExtendWith(SpringExtension.class)
class AppControllerUnitTest {

    @MockBean
    UserService userService;

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @Autowired
    AppController controller;

    @Autowired
    MockMvc mockMvc;

    @Value("${api.react-app.url}")
    String REACT_URL;

    TopTracksDTO tracksDTO;
    TopArtistsDTO artistsDTO;
    TopGenresDTO genresDTO;
    RecentlyPlayedDTO recentlyDTO;
    MainstreamScoreDTO mainstreamScoreDTO;
    UserDTO userDTO;

    @BeforeEach
    void prepare() {
         tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()), "1", "short");
         artistsDTO = new TopArtistsDTO("1", List.of(new ItemTopArtists()), "short");
         genresDTO = new TopGenresDTO(List.of(new Genre("rock", 2)), "short");
         recentlyDTO = new RecentlyPlayedDTO("1", List.of(new ItemRecentlyPlayed()));
         mainstreamScoreDTO = new MainstreamScoreDTO(75.00, "short");
         Image image = new Image();
         image.setUrl("imageurl");
         userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(image));
    }

    @Test
    void shouldAuthUser() throws Exception {
        when(spotifyAPIService.getCurrentUser()).thenReturn(userDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth").with(oauth2Login()))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(header().string("location", REACT_URL + "/callback?name=testuser&url=imageurl"));
    }

    @Test
    void shouldNotAuth() throws Exception {
        mockMvc.perform(get("/api/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDelete() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/api/delete").with(oauth2Login()
                .attributes(map -> map.put("userId", 1L)))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldGetTopTracksAuthenticated() throws Exception {
        when(spotifyAPIService.getTopTracks(1, "short")).thenReturn(tracksDTO);
        when(userService.compareTracks(1, tracksDTO)).thenReturn(tracksDTO);
        mockMvc.perform(get("/api/top/tracks").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                        .param("range", "short")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void shouldGetTopTracksNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetTopArtistsAuthenticated() throws Exception {
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        when(userService.compareArtists(1, artistsDTO)).thenReturn(artistsDTO);
        mockMvc.perform(get("/api/top/artists").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void shouldGetTopArtistsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetTopGenresAuthenticated() throws Exception {
        when(spotifyAPIService.getTopGenres(1, "short")).thenReturn(genresDTO);
        when(userService.compareGenres(1, genresDTO)).thenReturn(genresDTO);
        mockMvc.perform(get("/api/top/genres").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].genre", is("rock")))
                .andExpect(jsonPath("$.genres[0].score", is(2)));
    }

    @Test
    void shouldGetTopGenresNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetRecentlyPlayedAuthenticated() throws Exception {
        when(spotifyAPIService.getRecentlyPlayed())
                .thenReturn(recentlyDTO);
        mockMvc.perform(get("/api/recently").with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists());
    }

    @Test
    void shouldGetRecentlyPlayedNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/recently")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldGetMainstreamScoreAuthenticated() throws Exception {
        when(spotifyAPIService.getMainstreamScore(1, "short"))
                .thenReturn(mainstreamScoreDTO);
        when(userService.compareMainstream(1, mainstreamScoreDTO)).thenReturn(mainstreamScoreDTO);
        mockMvc.perform(get("/api/score").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", is(75.00)));
    }

    @Test
    void shouldGetMainstreamScoreNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/score")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldCreatePlaylistAuthenticated() throws Exception {
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        when(spotifyAPIService.postTopTracksPlaylist(1, "short"))
                .thenReturn(playlistDTO);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/playlist/create").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.external_urls").exists());
    }

    @Test
    void shouldCreatePlaylistNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/playlist/create").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isUnauthorized());
    }
}