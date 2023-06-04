package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestOAuth2RestTemplateConfig;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.repository.ArtistRepository;
import com.laa66.statlyapp.repository.GenreRepository;
import com.laa66.statlyapp.repository.TrackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
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
    void shouldGetCurrentUserResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getCurrentUser());
        mockServer.verify();
    }

    @Test
    void shouldGetTopTracksResponseOk() throws JsonProcessingException {
        TopTracksDTO data = new TopTracksDTO(List.of(new ItemTopTracks()),"1", "short", null);
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
    void shouldGetTopTracksResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getTopTracks(1, "short"));
        mockServer.verify();
    }

    @Test
    void shouldGetTopArtistsResponseOk() throws JsonProcessingException {
        TopArtistsDTO data = new TopArtistsDTO("1", new ArrayList<>(), "short", null);
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
    void shouldGetTopArtistsResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getTopArtists(1, "short"));
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
    void shouldGetRecentlyPlayedResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class,
                () -> spotifyAPIService.getRecentlyPlayed());
        mockServer.verify();
    }

    @Test
    void shouldGetTracksAnalysisResponseOk() throws JsonProcessingException {
        ResponseTracksAnalysis response = new ResponseTracksAnalysis(List.of(new TrackAnalysis(
                0.15,
                0.5,
                0.5,
                0.5,
                0.5,
                -30.0,
                0.5,
                0.5,
                0.5
        )));
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(response)));
        ResponseTracksAnalysis returnResponse = spotifyAPIService.getTracksAnalysis("id");
        mockServer.verify();
        assertEquals(1, returnResponse.getTracksAnalysis().size());
        assertEquals(0.15, returnResponse.getTracksAnalysis().get(0).getAcousticness());
        assertEquals(0.5, returnResponse.getTracksAnalysis().get(0).getValence());
    }

    @Test
    void shouldGetTracksAnalysisResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class,
                () -> spotifyAPIService.getTracksAnalysis("id"));
        mockServer.verify();
    }

    @Test
    void shouldGetUserPlaylistsResponseOk() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        ResponsePlaylists responsePlaylists = new ResponsePlaylists("url", 2, List.of(
                new Playlist(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new Playlist(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(userDTO)));

        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.USER_PLAYLISTS.get()
                        .replace("user_id", "testuser")
                        .replace("offset_num", "0")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(responsePlaylists)));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists(0);
        mockServer.verify();
        assertEquals(2, returnPlaylists.getPlaylists().size());
        assertEquals(2, returnPlaylists.getTotal());
        assertEquals("url", returnPlaylists.getNext());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id2", returnPlaylists.getPlaylists().get(1).getId());
    }

    @Test
    void shouldGetUserPlaylistsResponseError() throws JsonProcessingException {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(userDTO)));
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.USER_PLAYLISTS.get()
                                .replace("user_id", "testuser")
                                .replace("offset_num", "0")))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getUserPlaylists(0));
    }

    @Test
    void shouldPostTopTracksResponseOk() throws JsonProcessingException {
        UserDTO user = new UserDTO("1", "test@mail.com", "user", new ArrayList<>());
        ItemTopTracks track = new ItemTopTracks();
        track.setUri("trackId");
        TopTracksDTO topTracks = new TopTracksDTO(List.of(track), "1", "short", null);
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