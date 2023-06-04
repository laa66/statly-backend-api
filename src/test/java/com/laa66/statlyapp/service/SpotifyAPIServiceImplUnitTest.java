package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIEmptyResponseException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
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
        TopTracksDTO dto = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2", "long", null);
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        when(statsService.compareTracks(1, dto)).thenReturn(dto);
        TopTracksDTO returnDto = spotifyAPIService.getTopTracks(1, "long");
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getRange(), returnDto.getRange());
    }

    @Test
    void shouldGetTopTracksEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTopTracks(1, "long"));
    }

    @Test
    void shouldGetTopTracksWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () ->  spotifyAPIService.getTopTracks(1, "wrong"));
    }

    @Test
    void shouldGetTopArtistsWithValidUrl() {
        TopArtistsDTO dto = new TopArtistsDTO("1", List.of(new ItemTopArtists(), new ItemTopArtists()), "long", null);
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        when(statsService.compareArtists(1, dto)).thenReturn(dto);
        TopArtistsDTO returnDto = spotifyAPIService.getTopArtists(1, "long");
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getRange(), returnDto.getRange());
    }

    @Test
    void shouldGetTopArtistsEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTopArtists(1, "long"));
    }

    @Test
    void shouldGetTopArtistsWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS.get() + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopArtists(1, "wrong"));
    }

    @Test
    void shouldGetRecentlyPlayed() {
        RecentlyPlayedDTO dto = new RecentlyPlayedDTO("2", List.of(new ItemRecentlyPlayed(), new ItemRecentlyPlayed()));
        when(restTemplate.exchange(eq(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get()),
                eq(HttpMethod.GET), any(), eq(RecentlyPlayedDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        RecentlyPlayedDTO returnDto = spotifyAPIService.getRecentlyPlayed();
        assertEquals(dto.getItemRecentlyPlayedList().size(), returnDto.getItemRecentlyPlayedList().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTracksAnalysis() {
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
        ResponseTracksAnalysis returnResponse = spotifyAPIService.getTracksAnalysis("id");
        assertEquals(1, returnResponse.getTracksAnalysis().size());
        assertEquals(0.15, returnResponse.getTracksAnalysis().get(0).getAcousticness());
        assertEquals(0.5, returnResponse.getTracksAnalysis().get(0).getValence());
    }

    @Test
    void shouldGetTracksAnalysisEmptyBody() {
        when(restTemplate.exchange(eq(SpotifyAPI.TRACKS_ANALYSIS.get() + "id"),
                eq(HttpMethod.GET), any(), eq(ResponseTracksAnalysis.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.getTracksAnalysis("id"));
    }

    @Test
    void shouldGetUserPlaylists() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        ResponsePlaylists responsePlaylists = new ResponsePlaylists("url", 2, List.of(
                new Playlist(new SpotifyURL(), "id1", List.of(), "playlist1", new User()),
                new Playlist(new SpotifyURL(), "id2", List.of(), "playlist2", new User()))
        );
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.USER_PLAYLISTS.get()
                        .replace("user_id", "testuser")
                        .replace("offset_num", "0")),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(responsePlaylists, HttpStatus.OK));
        ResponsePlaylists returnPlaylists = spotifyAPIService.getUserPlaylists(0);
        assertEquals(2, returnPlaylists.getPlaylists().size());
        assertEquals(2, returnPlaylists.getTotal());
        assertEquals("url", returnPlaylists.getNext());
        assertEquals("id1", returnPlaylists.getPlaylists().get(0).getId());
        assertEquals("id2", returnPlaylists.getPlaylists().get(1).getId());
    }

    @Test
    void shouldGetUserPlaylistsEmptyBody() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.USER_PLAYLISTS.get()
                        .replace("user_id", "testuser")
                        .replace("offset_num", "0")),
                eq(HttpMethod.GET), any(), eq(ResponsePlaylists.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(SpotifyAPIEmptyResponseException.class,
                () -> spotifyAPIService.getUserPlaylists(0));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrl() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks(), new ItemTopTracks()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
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
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
       assertThrows(SpotifyAPIEmptyResponseException.class, () -> spotifyAPIService.postTopTracksPlaylist(1, "long"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrlIfPostingTracksFailed() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks(), new ItemTopTracks()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
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
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks(), new ItemTopTracks()), "2", "long", null);
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER.get()),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS.get() + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
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