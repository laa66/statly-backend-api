package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIEmptyResponseException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.impl.SpotifyAPIServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpotifyAPIServiceImplUnitTest {

    @Mock
    @Qualifier("restTemplateInterceptor")
    RestTemplate restTemplate;

    @Mock
    StatsService statsService;

    @InjectMocks
    SpotifyAPIServiceImpl spotifyAPIService;

    @Test
    void shouldGetCurrentUser() {
        UserDTO dto = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        UserDTO returnDto = spotifyAPIService.getCurrentUser();
        assertEquals(dto.getId(), returnDto.getId());
        assertEquals(dto.getEmail(), returnDto.getEmail());
        assertEquals(dto.getDisplayName(), returnDto.getDisplayName());
        assertEquals(1, dto.getImages().size());
    }

    @Test
    void shouldGetTopTracksWithValidUrl() {
        TracksDTO dto = new TracksDTO(List.of(new Track()
                , new Track()), "2", "long", null);
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        when(statsService.compareTracks(1, dto)).thenReturn(dto);
        TracksDTO returnDto = spotifyAPIService.getTopTracks(1, "long");
        assertEquals(dto.getTracks().size(), returnDto.getTracks().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getRange(), returnDto.getRange());
    }

    @Test
    void shouldGetTopTracksEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTopTracks(1, "long"));
    }

    @Test
    void shouldGetTopTracksWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () ->  spotifyAPIService.getTopTracks(1, "wrong"));
    }

    @Test
    void shouldGetTopArtistsWithValidUrl() {
        ArtistsDTO dto = new ArtistsDTO("1", List.of(new Artist(), new Artist()), "long", null);
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(ArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        when(statsService.compareArtists(1, dto)).thenReturn(dto);
        ArtistsDTO returnDto = spotifyAPIService.getTopArtists(1, "long");
        assertEquals(dto.getArtists().size(), returnDto.getArtists().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getRange(), returnDto.getRange());
    }

    @Test
    void shouldGetTopArtistsEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(ArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTopArtists(1, "long"));
    }

    @Test
    void shouldGetTopArtistsWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(ArtistsDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopArtists(1, "wrong"));
    }

    @Test
    void shouldGetRecentlyPlayed() {
        RecentlyPlayedDTO dto = new RecentlyPlayedDTO("2", List.of(new PlaybackEvent(), new PlaybackEvent()));
        when(restTemplate.exchange(eq(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()),
                eq(HttpMethod.GET), any(), eq(RecentlyPlayedDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        RecentlyPlayedDTO returnDto = spotifyAPIService.getRecentlyPlayed();
        assertEquals(dto.getPlaybackEvents().size(), returnDto.getPlaybackEvents().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTracksAnalysis() {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(
                new Album(),
                List.of(),
                "name",
                50,
                "uri",
                new SpotifyURL(),
                "id",
                0)), "1", "long", LocalDate.now());
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
        when(restTemplate.exchange(eq(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"),
                eq(HttpMethod.GET), any(), eq(ResponseTracksAnalysis.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        ResponseTracksAnalysis returnResponse = spotifyAPIService.getTracksAnalysis(tracksDTO);
        assertEquals(1, returnResponse.getTracksAnalysis().size());
        assertEquals(0.15, returnResponse.getTracksAnalysis().get(0).getAcousticness());
        assertEquals(0.5, returnResponse.getTracksAnalysis().get(0).getValence());
    }

    @Test
    void shouldGetTracksAnalysisEmptyBody() {
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(
                new Album(),
                List.of(),
                "name",
                50,
                "uri",
                new SpotifyURL(),
                "id",
                0)), "1", "long", LocalDate.now());
        when(restTemplate.exchange(eq(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"),
                eq(HttpMethod.GET), any(), eq(ResponseTracksAnalysis.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTracksAnalysis(tracksDTO));
    }

    @Test
    void shouldGetUserPlaylistsUnder50() {
        ResponsePlaylists responsePlaylists = new ResponsePlaylists(null, 2, List.of(
                new PlaylistInfo(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new PlaylistInfo(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER_PLAYLISTS.get()),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists, HttpStatus.OK));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists(null);
        assertEquals(2, returnPlaylists.getPlaylists().size());
        assertEquals(2, returnPlaylists.getTotal());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id2", returnPlaylists.getPlaylists().get(1).getId());
    }

    @Test
    void shouldGetUserPlaylistsUnder50Parameter() {
        ResponsePlaylists responsePlaylists = new ResponsePlaylists(null, 2, List.of(
                new PlaylistInfo(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new PlaylistInfo(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        when(restTemplate.exchange(eq(SpotifyAPI.USER_PLAYLISTS.get().replace("user_id", "username")),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists, HttpStatus.OK));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists("username");
        assertEquals(2, returnPlaylists.getPlaylists().size());
        assertEquals(2, returnPlaylists.getTotal());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id2", returnPlaylists.getPlaylists().get(1).getId());
    }

    @Test
    void shouldGetUserPlaylistsAbove50() {
        ResponsePlaylists responsePlaylists1 = new ResponsePlaylists("next", 5, List.of(
                new PlaylistInfo(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new PlaylistInfo(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        ResponsePlaylists responsePlaylists2 = new ResponsePlaylists(null, 5, List.of(
                new PlaylistInfo(new SpotifyURL(), "id3", List.of(), "playlist3", new User()),
                new PlaylistInfo(new SpotifyURL(), "id4", List.of(), "playlist4", new User()),
                new PlaylistInfo(new SpotifyURL(), "id5", List.of(), "playlist5", new User()))

        );
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER_PLAYLISTS.get()),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists1, HttpStatus.OK));
        when(restTemplate.exchange(eq(responsePlaylists1.getNext()),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists2, HttpStatus.OK));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists(null);
        assertEquals(5, returnPlaylists.getPlaylists().size());
        assertEquals(5, returnPlaylists.getTotal());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id5", returnPlaylists.getPlaylists().get(4).getId());
    }

    @Test
    void shouldGetUserPlaylistsAbove50Parameter() {
        ResponsePlaylists responsePlaylists1 = new ResponsePlaylists("next", 5, List.of(
                new PlaylistInfo(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new PlaylistInfo(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        ResponsePlaylists responsePlaylists2 = new ResponsePlaylists(null, 5, List.of(
                new PlaylistInfo(new SpotifyURL(), "id3", List.of(), "playlist3", new User()),
                new PlaylistInfo(new SpotifyURL(), "id4", List.of(), "playlist4", new User()),
                new PlaylistInfo(new SpotifyURL(), "id5", List.of(), "playlist5", new User()))

        );
        when(restTemplate.exchange(eq(SpotifyAPI.USER_PLAYLISTS.get().replace("user_id", "username")),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists1, HttpStatus.OK));
        when(restTemplate.exchange(eq(responsePlaylists1.getNext()),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists2, HttpStatus.OK));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists("username");
        assertEquals(5, returnPlaylists.getPlaylists().size());
        assertEquals(5, returnPlaylists.getTotal());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id5", returnPlaylists.getPlaylists().get(4).getId());
    }

    @Test
    void shouldGetUserPlaylistsEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER_PLAYLISTS.get()),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class,
                () -> spotifyAPIService.getUserPlaylists(null));
    }

    @Test
    void shouldGetPlaylistTracksUnder50() {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setId("id");
        playlistInfo.setName("playlist");
        Playlist playlist = new Playlist("random", null, List.of(
                new PlaylistTrack(), new PlaylistTrack(), new PlaylistTrack()
        ));
        when(restTemplate.exchange(eq(SpotifyAPI.PLAYLIST_TRACKS.get()
                .replace("playlist_id", "id")
                .replace("country_code", "ES")),
                eq(HttpMethod.GET), any(), eq(Playlist.class)))
                .thenReturn(new ResponseEntity<>(playlist, HttpStatus.OK));
        TracksDTO returnPlaylist = spotifyAPIService.getPlaylistTracks(playlistInfo, "ES");
        assertEquals(3, returnPlaylist.getTracks().size());
    }

    @Test
    void shouldGetPlaylistTracksAbove50() {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setId("id");
        playlistInfo.setName("playlist");
        Playlist playlist1 = new Playlist("first",
                SpotifyAPI.PLAYLIST_TRACKS.get() + "next", List.of(
                new PlaylistTrack(), new PlaylistTrack(), new PlaylistTrack()
        ));
        Playlist playlist2 = new Playlist("second", null, List.of(
                new PlaylistTrack(), new PlaylistTrack()
        ));
        when(restTemplate.exchange(eq(SpotifyAPI.PLAYLIST_TRACKS.get()
                        .replace("playlist_id", "id")
                        .replace("country_code", "ES")),
                eq(HttpMethod.GET), any(), eq(Playlist.class)))
                .thenReturn(new ResponseEntity<>(playlist1, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.PLAYLIST_TRACKS.get() + "next"),
                eq(HttpMethod.GET), any(), eq(Playlist.class)))
                .thenReturn(new ResponseEntity<>(playlist2, HttpStatus.OK));

        TracksDTO returnPlaylist = spotifyAPIService.getPlaylistTracks(playlistInfo, "ES");
        assertEquals(5, returnPlaylist.getTracks().size());
    }

    @Test
    void shouldGetPlaylistTracksEmptyBody() {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setId("id");
        playlistInfo.setName("playlist");
        when(restTemplate.exchange(eq(SpotifyAPI.PLAYLIST_TRACKS.get()
                        .replace("playlist_id", playlistInfo.getId())
                        .replace("country_code", "ES")),
                eq(HttpMethod.GET), any(), eq(Playlist.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class,
                () -> spotifyAPIService.getPlaylistTracks(playlistInfo, "ES"));
    }

    @Test
    void shouldGetPlaylistTracksWrongCountryCode() {
        PlaylistInfo playlistInfo = new PlaylistInfo();
        playlistInfo.setId("id");
        playlistInfo.setName("playlist");
        when(restTemplate.exchange(eq(SpotifyAPI.PLAYLIST_TRACKS.get()
                        .replace("playlist_id", "id")
                        .replace("country_code", "123")),
                eq(HttpMethod.GET), any(), eq(Playlist.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
        assertThrows(SpotifyAPIException.class,
                () -> spotifyAPIService.getPlaylistTracks(playlistInfo, "123"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrl() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(), new Track()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(statsService.compareTracks(eq(1L), any())).thenReturn(tracksDTO);
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.get().replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(snapshotId, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.EDIT_PLAYLIST_IMAGE.get().replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.PUT), any(), eq(Void.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));
        PlaylistDTO response = spotifyAPIService.postTopTracksPlaylist(1, "long");
        assertEquals(playlistDTO.getId(), response.getId());
        assertNotNull(playlistDTO.getUrl());
    }

    @Test
    void shouldPostTopTracksPlaylistWithNotValidUrl() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.CREATED));
        assertThrows(SpotifyAPIException.class, () -> spotifyAPIService.postTopTracksPlaylist(1, "wrong"));
    }

    @Test
    void shouldPostTopTracksPlaylistEmptyPlaylistBody() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.CREATED));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.postTopTracksPlaylist(1, "long"));
    }

    @Test
    void shouldPostTopTracksPlaylistEmptyTopTracksBody() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
       assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.postTopTracksPlaylist(1, "long"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrlIfPostingTracksFailed() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(), new Track()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(statsService.compareTracks(eq(1L), any())).thenReturn(tracksDTO);
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.get().replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(HttpClientErrorException.class);

        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.postTopTracksPlaylist(1, "long"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrlIfPuttingImageFailed() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(), new Track()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(statsService.compareTracks(eq(1L), any())).thenReturn(tracksDTO);
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.get().replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(snapshotId, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.EDIT_PLAYLIST_IMAGE.get().replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.PUT), any(), eq(Void.class)))
                .thenThrow(HttpClientErrorException.class);

        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.postTopTracksPlaylist(1, "long"));
    }
}