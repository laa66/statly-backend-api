package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
@ExtendWith(SpringExtension.class)
class AppControllerTest {

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @Autowired
    AppController controller;

    @Autowired
    MockMvc mockMvc;

    @Value("${dev.react-app.url}")
    String REACT_URL;

    static TopTracksDTO tracksDTO;
    static TopArtistsDTO artistsDTO;
    static TopGenresDTO genresDTO;
    static RecentlyPlayedDTO recentlyDTO;
    static MainstreamScoreDTO mainstreamScoreDTO;
    static UserIdDTO userIdDTO;

    @BeforeAll
    static void prepare() {
         tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()), "1");
         artistsDTO = new TopArtistsDTO("1", List.of(new ItemTopArtists()));
         genresDTO = new TopGenresDTO(List.of(new Genre("rock", 2)));
         recentlyDTO = new RecentlyPlayedDTO("1", List.of(new ItemRecentlyPlayed()));
         mainstreamScoreDTO = new MainstreamScoreDTO(75.00);
         Image image = new Image();
         image.setUrl("imageurl");
         userIdDTO = new UserIdDTO("testuser", "testuser", List.of(image));
    }

    @Test
    @WithMockUser
    void shouldAuthUser() throws Exception {
        when(spotifyAPIService.getCurrentUser()).thenReturn(userIdDTO);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth"))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(header().string("location", REACT_URL + "/callback?name=testuser&url=imageurl"))
                .andDo(print());
    }

    @Test
    void shouldNotAuth() throws Exception {
        mockMvc.perform(get("/api/top/tracks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopTracksAuthenticated() throws Exception {
        when(spotifyAPIService.getTopTracks("user", SpotifyAPI.TOP_TRACKS + "short_term"))
                .thenReturn(tracksDTO);
        mockMvc.perform(get("/api/top/tracks")
                        .param("range", "short")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists())
                .andDo(print());
    }

    @Test
    void shouldGetTopTracksNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/tracks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopTracksAuthenticatedWrongRange() throws Exception {
        when(spotifyAPIService.getTopTracks("user", SpotifyAPI.TOP_TRACKS + "wrong_term"))
                .thenReturn(null);
        mockMvc.perform(get("/api/top/tracks")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("range", "wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopArtistsAuthenticated() throws Exception {
        when(spotifyAPIService.getTopArtists("user", SpotifyAPI.TOP_ARTISTS + "short_term"))
                .thenReturn(artistsDTO);
        mockMvc.perform(get("/api/top/artists")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists())
                .andDo(print());
    }

    @Test
    void shouldGetTopArtistsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "short"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopArtistsAuthenticatedWrongRange() throws Exception {
        when(spotifyAPIService.getTopArtists("user", SpotifyAPI.TOP_ARTISTS + "wrong_term"))
                .thenReturn(null);
        mockMvc.perform(get("/api/top/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("range", "wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopGenresAuthenticated() throws Exception {
        when(spotifyAPIService.getTopGenres("user", SpotifyAPI.TOP_ARTISTS + "short_term"))
                .thenReturn(genresDTO);
        mockMvc.perform(get("/api/top/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres[0].genre", is("rock")))
                .andExpect(jsonPath("$.genres[0].score", is(2)))
                .andDo(print());
    }

    @Test
    void shouldGetTopGenresNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/top/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetTopGenresAuthenticatedWrongRange() throws Exception {
        when(spotifyAPIService.getTopGenres("user", SpotifyAPI.TOP_ARTISTS + "wrong_range"))
                .thenReturn(null);
        mockMvc.perform(get("/api/top/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetRecentlyPlayedAuthenticated() throws Exception {
        when(spotifyAPIService.getRecentlyPlayed("user"))
                .thenReturn(recentlyDTO);
        mockMvc.perform(get("/api/recently")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total", is("1")))
                .andExpect(jsonPath("$.items").exists())
                .andDo(print());
    }

    @Test
    void shouldGetRecentlyPlayedNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/recently")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetMainstreamScoreAuthenticated() throws Exception {
        when(spotifyAPIService.getMainstreamScore("user", SpotifyAPI.TOP_TRACKS + "short_term"))
                .thenReturn(mainstreamScoreDTO);
        mockMvc.perform(get("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", is(75.00)))
                .andDo(print());
    }

    @Test
    void shouldGetMainstreamScoreNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/score")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldGetMainstreamScoreWrongRange() throws Exception {
        when(spotifyAPIService.getMainstreamScore("user", SpotifyAPI.TOP_TRACKS + "wrong_term"))
                .thenReturn(null);
        mockMvc.perform(get("/api/score")
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "wrong"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldCreatePlaylistAuthenticated() throws Exception {
        when(spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "short_term"))
                .thenReturn("snapshot");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/playlist/create")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("snapshot"))
                .andDo(print());
    }

    @Test
    void shouldCreatePlaylistNotAuthenticated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/playlist/create").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

    @Test
    @WithMockUser
    void shouldCreatePlaylistAuthenticatedWrongRange() throws Exception {
        when(spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "short_term"))
                .thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/playlist/create")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("range", "short"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist())
                .andDo(print());
    }
}