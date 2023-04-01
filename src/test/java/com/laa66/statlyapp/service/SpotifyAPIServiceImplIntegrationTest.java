package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestOAuth2RestTemplateConfig;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
import com.laa66.statlyapp.model.SpotifyURL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = {TestOAuth2RestTemplateConfig.class, SpotifyAPIServiceImpl.class})
class SpotifyAPIServiceImplIntegrationTest {

    @Autowired
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @Autowired
    SpotifyAPIServiceImpl spotifyAPIService;

    MockRestServiceServer mockServer;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void shouldGetCurrentUserResponseOk() throws JsonProcessingException {
        UserIdDTO data = new UserIdDTO("1", "user", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));
        UserIdDTO response = spotifyAPIService.getCurrentUser();
        mockServer.verify();
        assertEquals(data.getId(), response.getId());
        assertEquals(data.getDisplayName(), response.getDisplayName());
        assertEquals(data.getImages().size(), response.getImages().size());
    }

    @Test
    void shouldGetCurrentUserResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getCurrentUser());
        mockServer.verify();
    }

    @Test
    void shouldGetCurrentUserResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getCurrentUser());
        mockServer.verify();
    }

    @Test
    void shouldGetTopTracksResponseOk() throws JsonProcessingException {
        TopTracksDTO data = new TopTracksDTO(List.of(new ItemTopTracks()),"1");
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));
        TopTracksDTO response = spotifyAPIService.getTopTracks("user", SpotifyAPI.TOP_TRACKS);
        mockServer.verify();
        assertEquals(data.getItemTopTracks().size(), response.getItemTopTracks().size());
        assertEquals(data.getTotal(), response.getTotal());
    }

    @Test
    void shouldGetTopTracksResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getTopTracks("user", SpotifyAPI.TOP_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldGetTopTracksResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getTopTracks("user", SpotifyAPI.TOP_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldGetTopArtistsResponseOk() throws JsonProcessingException {
        TopArtistsDTO data = new TopArtistsDTO("1", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        TopArtistsDTO response = spotifyAPIService.getTopArtists("user", SpotifyAPI.TOP_ARTISTS);
        mockServer.verify();
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getItemTopArtists().size(), response.getItemTopArtists().size());
    }

    @Test
    void shouldGetTopArtistsResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class, () -> spotifyAPIService.getTopArtists("user", SpotifyAPI.TOP_ARTISTS));
        mockServer.verify();
    }

    @Test
    void shouldGetTopArtistsResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class, () -> spotifyAPIService.getTopArtists("user", SpotifyAPI.TOP_ARTISTS));
        mockServer.verify();
    }

    @Test
    void shouldGetTopGenresResponseOk() throws JsonProcessingException {
        ItemTopArtists item1 = new ItemTopArtists(List.of("Rock", "Rap", "Rap"), new ArrayList<>(), "artist1", "uri", new SpotifyURL());
        ItemTopArtists item2 = new ItemTopArtists(List.of("Rock", "Rap"), new ArrayList<>(), "artist2", "uri", new SpotifyURL());
        TopArtistsDTO data = new TopArtistsDTO("2", List.of(item1, item2));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        TopGenresDTO response = spotifyAPIService.getTopGenres("user", SpotifyAPI.TOP_ARTISTS);
        mockServer.verify();
        assertEquals(2, response.getGenres().size());
        assertEquals("Rap", response.getGenres().get(0).getGenre());
        assertEquals("Rock", response.getGenres().get(1).getGenre());
    }

    @Test
    void shouldGetTopGenresResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopGenres("user", SpotifyAPI.TOP_ARTISTS));
        mockServer.verify();
    }

    @Test
    void shouldGetTopGenresResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getTopGenres("user", SpotifyAPI.TOP_ARTISTS));
        mockServer.verify();
    }

    @Test
    void shouldGetMainstreamScoreResponseOk() throws JsonProcessingException {
        ItemTopTracks item1 = new ItemTopTracks();
        item1.setPopularity(30);
        ItemTopTracks item2 = new ItemTopTracks();
        item2.setPopularity(70);
        TopTracksDTO data = new TopTracksDTO(List.of(item1, item2), "2");

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        MainstreamScoreDTO response = spotifyAPIService.getMainstreamScore("user", SpotifyAPI.TOP_TRACKS);
        mockServer.verify();
        assertEquals(50.00, response.getScore());
    }

    @Test
    void shouldGetMainstreamScoreResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getMainstreamScore("user", SpotifyAPI.TOP_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldGetMainstreamScoreResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getMainstreamScore("user", SpotifyAPI.TOP_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldGetRecentlyPlayedResponseOk() throws JsonProcessingException {
        RecentlyPlayedDTO data = new RecentlyPlayedDTO("2", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        RecentlyPlayedDTO response = spotifyAPIService.getRecentlyPlayed("user");
        mockServer.verify();
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getItemRecentlyPlayedList().size(), response.getItemRecentlyPlayedList().size());
    }

    @Test
    void shouldGetRecentlyPlayedResponseClientError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getMainstreamScore("user", SpotifyAPI.RECENTLY_PLAYED_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldGetRecentlyPlayedResponseServerError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThrows(HttpServerErrorException.class,
                () -> spotifyAPIService.getMainstreamScore("user", SpotifyAPI.RECENTLY_PLAYED_TRACKS));
        mockServer.verify();
    }

    @Test
    void shouldPostTopTracksResponseOk() throws JsonProcessingException {
        UserIdDTO user = new UserIdDTO("1", "user", new ArrayList<>());
        ItemTopTracks track = new ItemTopTracks();
        track.setUri("trackId");
        TopTracksDTO topTracks = new TopTracksDTO(List.of(track), "1");
        PlaylistDTO playlist = new PlaylistDTO("1", new SpotifyURL());
        String data = "playlist";

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(user)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id", user.getId())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playlist)));

        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(topTracks)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.ADD_PLAYLIST_TRACK.replace("playlist_id", playlist.getId())))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(data));

        PlaylistDTO response = spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "short_term");
        mockServer.verify();
        assertEquals(playlist.getId(), response.getId());
        assertNotNull(playlist.getUrl());
    }

    @Test
    void shouldPostTopTracksResponseWrongRange() throws JsonProcessingException {
        UserIdDTO user = new UserIdDTO("1", "user", new ArrayList<>());
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(user)));

        assertThrows(SpotifyAPIException.class,
                () -> spotifyAPIService.postTopTracksPlaylist("user", SpotifyAPI.TOP_TRACKS + "wrong"));
        mockServer.verify();
    }



}