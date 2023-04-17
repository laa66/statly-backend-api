package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
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

    @InjectMocks
    SpotifyAPIServiceImpl spotifyAPIService;

    @Test
    void shouldGetCurrentUser() {
        UserDTO dto = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
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
                , new ItemTopTracks()), "2");
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopTracksDTO returnDto = spotifyAPIService.getTopTracks("user"
                , "long");
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTopTracksWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () ->  spotifyAPIService.getTopTracks("user", "wrong"));
    }

    @Test
    void shouldGetTopArtistsWithValidUrl() {
        TopArtistsDTO dto = new TopArtistsDTO("1", List.of(new ItemTopArtists(), new ItemTopArtists()));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopArtistsDTO returnDto = spotifyAPIService.getTopArtists("user"
                , "long");
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldGetTopArtistsWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopArtists("user", "wrong"));
    }

    @Test
    void shouldGetMainstreamScoreWithValidUrl() {
        ItemTopTracks track1 = new ItemTopTracks();
        track1.setPopularity(40);
        ItemTopTracks track2 = new ItemTopTracks();
        track2.setPopularity(70);
        TopTracksDTO dto = new TopTracksDTO(List.of(track1, track2), "2");
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        MainstreamScoreDTO returnDto = spotifyAPIService.getMainstreamScore("user"
                , "long");
        assertEquals(55.00, returnDto.getScore());
    }

    @Test
    void shouldGetMainStreamScoreWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getMainstreamScore("user", "wrong"));
    }

    @Test
    void shouldGetTopGenresWithValidUrl() {
        ItemTopArtists artist1 = new ItemTopArtists();
        artist1.setGenres(List.of("classic", "classic", "classic", "rock", "rock", "rock", "rock"));
        ItemTopArtists artist2 = new ItemTopArtists();
        artist2.setGenres(List.of("classic", "classic", "classic"));
        TopArtistsDTO dto = new TopArtistsDTO();
        dto.setItemTopArtists(List.of(artist1, artist2));

        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        TopGenresDTO returnDto = spotifyAPIService.getTopGenres("user"
                , "long");

        assertEquals(2, returnDto.getGenres().size());
        assertEquals(60, returnDto.getGenres().get(0).getScore());
        assertEquals(40, returnDto.getGenres().get(1).getScore());
    }

    @Test
    void shouldGetTopGenresWithNotValidUrl() {
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_ARTISTS + "wrong_term"),
                eq(HttpMethod.GET), any(), eq(TopArtistsDTO.class)))
                .thenThrow(HttpClientErrorException.class);
        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.getTopGenres("user", "wrong"));
    }

    @Test
    void shouldGetRecentlyPlayed() {
        RecentlyPlayedDTO dto = new RecentlyPlayedDTO("2", List.of(new ItemRecentlyPlayed(), new ItemRecentlyPlayed()));
        when(restTemplate.exchange(eq(SpotifyAPI.RECENTLY_PLAYED_TRACKS),
                eq(HttpMethod.GET), any(), eq(RecentlyPlayedDTO.class)))
                .thenReturn(new ResponseEntity<>(dto, HttpStatus.OK));
        RecentlyPlayedDTO returnDto = spotifyAPIService.getRecentlyPlayed("user");
        assertEquals(dto.getItemRecentlyPlayedList().size(), returnDto.getItemRecentlyPlayedList().size());
        assertEquals(dto.getTotal(), returnDto.getTotal());
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrl() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(new Image()));
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2");
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());
        String snapshotId = "snapshotId";

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(snapshotId, HttpStatus.CREATED));
        PlaylistDTO response = spotifyAPIService.postTopTracksPlaylist("user", "long");
        assertEquals(playlistDTO.getId(), response.getId());
        assertNotNull(playlistDTO.getUrl());

    }

    @Test
    void shouldPostTopTracksPlaylistWithNotValidUrl() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.CREATED));
        assertThrows(SpotifyAPIException.class, () -> spotifyAPIService.postTopTracksPlaylist("user", "wrong"));
    }

    @Test
    void shouldPostTopTracksPlaylistWithValidUrlIfPostingFailed() {
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com","testuser", List.of(new Image()));
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(new ItemTopTracks()
                , new ItemTopTracks()), "2");
        PlaylistDTO playlistDTO = new PlaylistDTO("1", new SpotifyURL());

        when(restTemplate.exchange(eq(SpotifyAPI.CURRENT_USER),
                eq(HttpMethod.GET), any(), eq(UserDTO.class)))
                .thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.TOP_TRACKS + "long_term"),
                eq(HttpMethod.GET), any(), eq(TopTracksDTO.class)))
                .thenReturn(new ResponseEntity<>(tracksDTO, HttpStatus.OK));
        when(restTemplate.exchange(eq(SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id",
                userDTO.getId())), eq(HttpMethod.POST), any(), eq(PlaylistDTO.class)))
                .thenReturn(new ResponseEntity<>(playlistDTO, HttpStatus.CREATED));
        when(restTemplate.exchange(eq(SpotifyAPI.ADD_PLAYLIST_TRACK.replace("playlist_id",
                playlistDTO.getId())), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenThrow(HttpClientErrorException.class);

        assertThrows(HttpClientErrorException.class,
                () -> spotifyAPIService.postTopTracksPlaylist("user", "long"));
    }
}