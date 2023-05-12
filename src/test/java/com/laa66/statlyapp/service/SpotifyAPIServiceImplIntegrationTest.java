package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestOAuth2RestTemplateConfig;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
import com.laa66.statlyapp.model.SpotifyURL;
import com.laa66.statlyapp.repository.ArtistRepository;
import com.laa66.statlyapp.repository.GenreRepository;
import com.laa66.statlyapp.repository.MainstreamRepository;
import com.laa66.statlyapp.repository.TrackRepository;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = {TestOAuth2RestTemplateConfig.class, SpotifyAPIServiceImpl.class, StatsServiceImpl.class})
class SpotifyAPIServiceImplIntegrationTest {

    @Autowired
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @Autowired
    StatsServiceImpl statsService;

    @MockBean
    TrackRepository trackRepository;

    @MockBean
    ArtistRepository artistRepository;

    @MockBean
    GenreRepository genreRepository;

    @MockBean
    MainstreamRepository mainstreamRepository;

    @Autowired
    @InjectMocks
    SpotifyAPIServiceImpl spotifyAPIService;

    MockRestServiceServer mockServer;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldGetCurrentUserResponseOk() throws JsonProcessingException {
        UserDTO data = new UserDTO("1", "test@mail.com", "user", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));
        UserDTO response = spotifyAPIService.getCurrentUser();
        mockServer.verify();
        assertEquals(data.getId(), response.getId());
        assertEquals(data.getEmail(), response.getEmail());
        assertEquals(data.getDisplayName(), response.getDisplayName());
        assertEquals(data.getImages().size(), response.getImages().size());
    }

    @Test
    void shouldGetCurrentUserResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getCurrentUser());
        mockServer.verify();
    }

    @Test
    void shouldGetCurrentUserResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getCurrentUser());
        mockServer.verify();
    }

    @Test
    void shouldGetTopTracksResponseOk() throws JsonProcessingException {
        TopTracksDTO data = new TopTracksDTO(List.of(new ItemTopTracks()),"1", "short");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));
        TopTracksDTO response = spotifyAPIService.getTopTracks(1, "short");
        mockServer.verify();
        assertEquals(data.getItemTopTracks().size(), response.getItemTopTracks().size());
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getRange(), response.getRange());
    }

    @Test
    void shouldGetTopTracksResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getTopTracks(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopTracksResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getTopTracks(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopArtistsResponseOk() throws JsonProcessingException {
        TopArtistsDTO data = new TopArtistsDTO("1", new ArrayList<>(), "short");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        TopArtistsDTO response = spotifyAPIService.getTopArtists(1, "short");
        mockServer.verify();
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getItemTopArtists().size(), response.getItemTopArtists().size());
        assertEquals(data.getRange(), response.getRange());
    }

    @Test
    void shouldGetTopArtistsResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getTopArtists(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopArtistsResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getTopArtists(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopGenresResponseOk() throws JsonProcessingException {
        ItemTopArtists item1 = new ItemTopArtists(List.of("Rock", "Rap", "Rap"), new ArrayList<>(), "artist1", "uri", new SpotifyURL(), 0);
        ItemTopArtists item2 = new ItemTopArtists(List.of("Rock", "Rap"), new ArrayList<>(), "artist2", "uri", new SpotifyURL(), 0);
        TopArtistsDTO data = new TopArtistsDTO("2", List.of(item1, item2), "short");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        TopGenresDTO response = spotifyAPIService.getTopGenres(1, "short");
        mockServer.verify();
        assertEquals(2, response.getGenres().size());
        assertEquals("Rap", response.getGenres().get(0).getGenre());
        assertEquals("Rock", response.getGenres().get(1).getGenre());
        assertEquals(data.getRange(), response.getRange());
    }

    @Test
    void shouldGetTopGenresResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopGenres(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopGenresResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getTopGenres(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetMainstreamScoreResponseOk() throws JsonProcessingException {
        ItemTopTracks item1 = new ItemTopTracks();
        item1.setPopularity(30);
        ItemTopTracks item2 = new ItemTopTracks();
        item2.setPopularity(70);
        TopTracksDTO data = new TopTracksDTO(List.of(item1, item2), "2", "short");

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        MainstreamScoreDTO response = spotifyAPIService.getMainstreamScore(1, "short");
        mockServer.verify();
        assertEquals(50.00, response.getScore());
        assertEquals(data.getRange(), response.getRange());
    }

    @Test
    void shouldGetMainstreamScoreResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getMainstreamScore(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetMainstreamScoreResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getMainstreamScore(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetRecentlyPlayedResponseOk() throws JsonProcessingException {
        RecentlyPlayedDTO data = new RecentlyPlayedDTO("2", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        RecentlyPlayedDTO response = spotifyAPIService.getRecentlyPlayed();
        mockServer.verify();
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getItemRecentlyPlayedList().size(), response.getItemRecentlyPlayedList().size());
    }

    @Test
    void shouldGetRecentlyPlayedResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getRecentlyPlayed());
        mockServer.verify();
    }

    @Test
    void shouldGetRecentlyPlayedResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getRecentlyPlayed());
        mockServer.verify();
    }

    @Test
    void shouldPostTopTracksResponseOk() throws JsonProcessingException {
        UserDTO user = new UserDTO("1", "test@mail.com", "user", new ArrayList<>());
        ItemTopTracks track = new ItemTopTracks();
        track.setUri("trackId");
        TopTracksDTO topTracks = new TopTracksDTO(List.of(track), "1", "short");
        PlaylistDTO playlist = new PlaylistDTO("1", new SpotifyURL());
        String data = "playlist";

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(user)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id", user.getId())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playlist)));

        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(topTracks)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.ADD_PLAYLIST_TRACK.get().replace("playlist_id", playlist.getId())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(data));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.EDIT_PLAYLIST_IMAGE.get().replace("playlist_id", playlist.getId())))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.ACCEPTED)
                        .contentType(MediaType.IMAGE_JPEG));

        PlaylistDTO response = spotifyAPIService.postTopTracksPlaylist(1, "short");
        mockServer.verify();
        assertEquals(playlist.getId(), response.getId());
        assertNotNull(playlist.getUrl());
    }

    @Test
    void shouldPostTopTracksResponseWrongRange() throws JsonProcessingException {
        UserDTO user = new UserDTO("1", "test@mail.com", "user", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(user)));

        assertThrows(SpotifyAPIException.class,
                () -> spotifyAPIService.postTopTracksPlaylist(1, SpotifyAPI.TOP_TRACKS.get() + "wrong"));
        mockServer.verify();
    }
}