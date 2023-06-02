package com.laa66.statlyapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
@ExtendWith(SpringExtension.class)
@Import({TestSecurityConfig.class})
@MockBeans({@MockBean(TrackRepository.class), @MockBean(ArtistRepository.class), @MockBean(GenreRepository.class),
        @MockBean(MainstreamRepository.class), @MockBean(UserRepository.class), @MockBean(BetaUserRepository.class)})
class ApiControllerUnitTest {

    @MockBean
    LibraryAnalysisService libraryAnalysisService;

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplateInterceptor;

    @MockBean
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    @Autowired
    ApiController controller;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Value("${api.react-app.url}")
    String REACT_URL;

    @Test
    void shouldGetTopTracksAuthenticated() throws Exception {
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()), "1", "short", null);
        when(spotifyAPIService.getTopTracks(1, "short")).thenReturn(tracksDTO);
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
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetTopArtistsAuthenticated() throws Exception {
        TopArtistsDTO artistsDTO = new TopArtistsDTO("1", List.of(new ItemTopArtists()), "short", null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
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
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetTopGenresAuthenticated() throws Exception {
        TopGenresDTO genresDTO = new TopGenresDTO(List.of(new Genre("rock", 2)), "short", null);
        TopArtistsDTO artistsDTO = new TopArtistsDTO("1", List.of(new ItemTopArtists()), "short", null);
        when(spotifyAPIService.getTopArtists(1, "short")).thenReturn(artistsDTO);
        when(libraryAnalysisService.getTopGenres(1, "short", artistsDTO)).thenReturn(genresDTO);
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
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetRecentlyPlayedAuthenticated() throws Exception {
        RecentlyPlayedDTO recentlyDTO = new RecentlyPlayedDTO("1", List.of(new ItemRecentlyPlayed()));
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
                .andExpect(status().isFound());
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
                .andExpect(status().isFound());
    }
}