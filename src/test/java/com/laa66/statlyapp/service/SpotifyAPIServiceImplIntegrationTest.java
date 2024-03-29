package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestOAuth2RestTemplateConfig;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.spotify.response.ResponsePlaylists;
import com.laa66.statlyapp.model.spotify.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.model.spotify.*;
import com.laa66.statlyapp.repository.ArtistRepository;
import com.laa66.statlyapp.repository.GenreRepository;
import com.laa66.statlyapp.repository.TrackRepository;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.impl.SpotifyAPIServiceImpl;
import com.laa66.statlyapp.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServiceUnavailable;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(classes = {TestOAuth2RestTemplateConfig.class, SpotifyAPIServiceImpl.class, StatsServiceImpl.class})
@MockBeans({@MockBean(TrackRepository.class), @MockBean(ArtistRepository.class),
@MockBean(GenreRepository.class), @MockBean(UserRepository.class)})
class SpotifyAPIServiceImplIntegrationTest {

    @Autowired
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @Autowired
    StatsServiceImpl statsService;

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
        UserDTO data = UserDTO.builder()
                .id("1")
                .email("test@mail.com")
                .name("user")
                .images(List.of())
                .build();
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
        assertEquals(data.getName(), response.getName());
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
        TracksDTO data = new TracksDTO(List.of(new Track()),"1", "short", null, null);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_TRACKS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));
        TracksDTO response = spotifyAPIService.getTopTracks(1, "short");
        mockServer.verify();
        assertEquals(data.getTracks().size(), response.getTracks().size());
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
        ArtistsDTO data = new ArtistsDTO("1", new ArrayList<>(), "short", null, null);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TOP_ARTISTS.get() + "short_term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(data)));

        ArtistsDTO response = spotifyAPIService.getTopArtists(1, "short");
        mockServer.verify();
        assertEquals(data.getTotal(), response.getTotal());
        assertEquals(data.getArtists().size(), response.getArtists().size());
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
        assertEquals(data.getPlaybackEvents().size(), response.getPlaybackEvents().size());
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
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(
                new Album(),
                List.of(),
                "name",
                50,
                "uri",
                new SpotifyURL(),
                "id",
                0)), "1", "long", LocalDate.now(), null);
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
        ResponseTracksAnalysis returnResponse = spotifyAPIService.getTracksAnalysis(tracksDTO);
        mockServer.verify();
        assertEquals(1, returnResponse.getTracksAnalysis().size());
        assertEquals(0.15, returnResponse.getTracksAnalysis().get(0).getAcousticness());
        assertEquals(0.5, returnResponse.getTracksAnalysis().get(0).getValence());
    }

    @Test
    void shouldGetTracksAnalysisResponseError() {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(
                new Album(),
                List.of(),
                "name",
                50,
                "uri",
                new SpotifyURL(),
                "id",
                0)), "1", "long", LocalDate.now(), null);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class,
                () -> spotifyAPIService.getTracksAnalysis(tracksDTO));
        mockServer.verify();
    }

    @Test
    void shouldGetUserPlaylistsResponseOk() throws JsonProcessingException {
        ResponsePlaylists responsePlaylists = new ResponsePlaylists(null, 2, List.of(
                new PlaylistInfo(new SpotifyURL(), "id1", List.of(), "playlist1",
                        null),
                new PlaylistInfo(new SpotifyURL(), "id2", List.of(), "playlist2",
                        null)));
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.CURRENT_USER_PLAYLISTS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(responsePlaylists)));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists(null);
        mockServer.verify();
        assertEquals(2, returnPlaylists.getPlaylists().size());
        assertEquals(2, returnPlaylists.getTotal());
        assertNull(returnPlaylists.getNext());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id2", returnPlaylists.getPlaylists().get(1).getId());
    }

    @Test
    void shouldGetUserPlaylistsResponseError() {
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.CURRENT_USER_PLAYLISTS.get()))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getUserPlaylists(null));
        mockServer.verify();
    }

    @Test
    void shouldGetPlaylistTracksResponseOk() throws JsonProcessingException {
        PlaylistInfo playlistInfo = new PlaylistInfo(null, "id", null, "playlist", null);
        Playlist playlist = new Playlist("random", null, List.of(
                new PlaylistTrack(), new PlaylistTrack(), new PlaylistTrack()
        ));
        mockServer.expect(ExpectedCount.once(),
                requestTo(SpotifyAPI.PLAYLIST_TRACKS.get()
                        .replace("playlist_id", "id")
                        .replace("country_code", "ES")))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(playlist)));
        TracksDTO returnPlaylist = spotifyAPIService.getPlaylistTracks(playlistInfo, "ES");
        mockServer.verify();
        assertEquals(3, returnPlaylist.getTracks().size());
    }

    @Test
    void shouldGetPlaylistTracksResponseError() {
        PlaylistInfo playlistInfo = new PlaylistInfo(null, "id", null, "playlist", null);
        mockServer.expect(ExpectedCount.once(),
                        requestTo(SpotifyAPI.PLAYLIST_TRACKS.get()
                                .replace("playlist_id", "id")
                                .replace("country_code", "ES")))
                .andRespond(withServiceUnavailable());
        assertThrows(RestClientException.class, () -> spotifyAPIService.getPlaylistTracks(playlistInfo, "ES"));
        mockServer.verify();
    }

    @Test
    void shouldPostTopTracksResponseOk() throws JsonProcessingException {
        UserDTO user = UserDTO.builder()
                .id("1")
                .uri("uri")
                .email("test@mail.com")
                .name("user")
                .images(List.of())
                .points(0)
                .build();
        Track track = new Track();
        track.setUri("trackId");
        TracksDTO topTracks = new TracksDTO(List.of(track), "1", "short", null, null);
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
        UserDTO user = UserDTO.builder()
                .id("1")
                .uri("uri")
                .email("test@mail.com")
                .name("user")
                .images(List.of())
                .points(0)
                .build();
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