package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.exception.TooManyRequestsException;
import com.laa66.statlyapp.model.spotify.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.model.spotify.*;
import com.laa66.statlyapp.service.impl.LibraryAnalysisServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryAnalysisServiceImplUnitTest {

    @Mock
    SocialService socialService;

    @Mock
    StatsService statsService;

    @Mock
    SpotifyAPIService spotifyAPIService;

    @Spy
    @InjectMocks
    LibraryAnalysisServiceImpl libraryAnalysisService;

    @Test
    void shouldGetTopGenresArtistsValid() {
        Artist artist1 = new Artist();
        artist1.setGenres(List.of("classic", "classic", "classic", "rock", "rock", "rock", "rock"));
        Artist artist2 = new Artist();
        artist2.setGenres(List.of("classic", "classic", "classic"));
        ArtistsDTO artistsDTO = new ArtistsDTO("2", List.of(artist1, artist2), "long", null, null);
        GenresDTO genresDTO = new GenresDTO(List.of(new Genre("classic", 60), new Genre("rock", 40)), "long", null, null);
        when(statsService.compareGenres(eq(1L), any())).thenReturn(genresDTO);
        GenresDTO returnDto = libraryAnalysisService.getTopGenres(1, "long", artistsDTO);

        assertEquals(2, returnDto.getGenres().size());
        assertEquals(60, returnDto.getGenres().get(0).getScore());
        assertEquals(40, returnDto.getGenres().get(1).getScore());
        assertEquals(artistsDTO.getRange(), returnDto.getRange());
    }

    @Test
    void shouldGetTopGenresArtistEmpty() {
        assertThrows(RuntimeException.class,
                () -> libraryAnalysisService.getTopGenres(1, "long", null));
        assertThrows(RuntimeException.class,
                () -> libraryAnalysisService.getTopGenres(1, "long", new ArtistsDTO("0", null, "long", LocalDate.now(), null)));
    }

    @Test
    void shouldGetLibraryAnalysis() {
        TracksDTO tracksDTO = new TracksDTO(List.of(
                new Track(new Album(List.of(new Image()), "album1", List.of()), List.of(), "name", 35, "uri", new SpotifyURL(), "id1", 0),
                new Track(new Album(List.of(new Image(), new Image()), "album2", List.of()), List.of(), "name", 75, "uri", new SpotifyURL(), "id2", 0)
        ), "2", "long", LocalDate.now(), null);
        ResponseTracksAnalysis tracksAnalysis = new ResponseTracksAnalysis(List.of(
                new TrackAnalysis(0.6, 0.5, 0.15, 0.23, 0.8, -6.0, 0.22, 120.0, 0.5),
                new TrackAnalysis(0.4, 0.3, 0.23, 0.13, 0.14, -45.0, 0.52, 150.5, 0.98),
                new TrackAnalysis(0.5, 0.1, 0.2, 0.3, 0.7, 0, 0.24, 98, 0.5)
        ));
        when(spotifyAPIService.getTracksAnalysis(tracksDTO))
                .thenReturn(tracksAnalysis);
        AnalysisDTO analysisDTO = libraryAnalysisService.getTracksAnalysis(tracksDTO, 1L);

        verify(statsService, times(1)).saveUserStats(isA(Long.class), anyMap());
        assertEquals(50.0, analysisDTO.getAnalysis().get("acousticness"));
        assertEquals(30.0, analysisDTO.getAnalysis().get("danceability"));
        assertEquals(19.0, analysisDTO.getAnalysis().get("energy"));
        assertEquals(22.0, analysisDTO.getAnalysis().get("instrumentalness"));
        assertEquals(55.0, analysisDTO.getAnalysis().get("liveness"));
        assertEquals(-17, analysisDTO.getAnalysis().get("loudness"));
        assertEquals(33.0, analysisDTO.getAnalysis().get("speechiness"));
        assertEquals(123.0, analysisDTO.getAnalysis().get("tempo"));
        assertEquals(66.0, analysisDTO.getAnalysis().get("valence"));
        assertEquals(55.0, analysisDTO.getAnalysis().get("mainstream"));
        assertEquals(238.0, analysisDTO.getAnalysis().get("boringness"));
        assertEquals(2, analysisDTO.getImages().size());
    }

    @Test
    void shouldGetLibraryAnalysisNullParameter() {
        assertThrows(RuntimeException.class, () -> libraryAnalysisService.getTracksAnalysis(null, null));
    }

    @Test
    void shouldGetUsersMatching() {
        when(statsService.matchTracks(1,2)).thenReturn(Pair.of(40, 120));
        when(statsService.matchArtists(1,2)).thenReturn(Pair.of(34, 70));
        when(statsService.matchGenres(1,2)).thenReturn(Pair.of(4, 15));

        Map<String, Double> usersMatching = libraryAnalysisService.getUsersMatching(1, 2);
        assertEquals(33., usersMatching.get("track"));
        assertEquals(49., usersMatching.get("artist"));
        assertEquals(27., usersMatching.get("genre"));
        assertEquals(37., usersMatching.get("overall"));
    }

    @Test
    void shouldGetUsersMatchingNotValidUsersIds() {
        when(statsService.matchTracks(1,2)).thenReturn(Pair.of(0, 0));
        when(statsService.matchArtists(1,2)).thenReturn(Pair.of(0, 0));
        when(statsService.matchGenres(1,2)).thenReturn(Pair.of(0, 0));
        Map<String, Double> firstNotValid = libraryAnalysisService.getUsersMatching(1,2);
        assertEquals(0.0, firstNotValid.get("overall"));

        when(statsService.matchTracks(3,4)).thenReturn(Pair.of(0, 30));
        when(statsService.matchArtists(3,4)).thenReturn(Pair.of(0, 50));
        when(statsService.matchGenres(3,4)).thenReturn(Pair.of(0, 7));
        Map<String, Double> secondNotValid = libraryAnalysisService.getUsersMatching(3,4);
        assertEquals(0.0, secondNotValid.get("overall"));

    }

    @Test
    void shouldCreatePlaylistBattle() {
        TracksDTO tracks1 = new TracksDTO();
        TracksDTO tracks2 = new TracksDTO();
        AnalysisDTO analysis1 = new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of()// 315
        );
        AnalysisDTO analysis2 = new AnalysisDTO(
                Map.of("tempo", 110.,
                        "valence", .30,
                        "energy", .55,
                        "danceability", .15,
                        "mainstream", 65.,
                        "boringness", 210.),
                List.of()// 245
        );
        doReturn(analysis1).when(libraryAnalysisService)
                .getTracksAnalysis(tracks1, null);
        doReturn(analysis2).when(libraryAnalysisService)
                .getTracksAnalysis(tracks2, null);
        when(statsService.isBattlePossible(1, 2)).thenReturn(true);
        BattleResultDTO battleResultDTO = libraryAnalysisService.createPlaylistBattle(1L, 2L,
                tracks1, tracks2);
        verify(socialService, times(2)).updatePoints(anyLong(), anyInt());
        assertEquals(1L, battleResultDTO.getWinner().getId());
        assertEquals(2L, battleResultDTO.getLoser().getId());
        assertEquals(70., battleResultDTO.getResult());
        assertEquals(315., battleResultDTO.getWinner().getScore());
        assertEquals(245., battleResultDTO.getLoser().getScore());
    }

    @Test
    void shouldCreatePlaylistBattleNoContest() {
        TracksDTO tracks1 = new TracksDTO();
        TracksDTO tracks2 = new TracksDTO();
        AnalysisDTO analysis1 = new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of()// 315
        );
        AnalysisDTO analysis2 = new AnalysisDTO(Map.of("tempo", 120.,
                "valence", .35,
                "energy", .60,
                "danceability", .45,
                "mainstream", 45.,
                "boringness", 260.),
                List.of()// 315
        );
        doReturn(analysis1).when(libraryAnalysisService)
                .getTracksAnalysis(tracks1, null);
        doReturn(analysis2).when(libraryAnalysisService)
                .getTracksAnalysis(tracks2, null);
        when(statsService.isBattlePossible(1, 2)).thenReturn(true);
        BattleResultDTO battleResultDTO = libraryAnalysisService.createPlaylistBattle(1L, 2L,
                tracks1, tracks2);
        assertEquals(0., battleResultDTO.getResult());
        assertEquals(315., battleResultDTO.getWinner().getScore());
        assertEquals(315., battleResultDTO.getLoser().getScore());
    }

    @Test
    void shouldCreatePlaylistBattleTooManyBattle() {
        when(statsService.isBattlePossible(1,2)).thenReturn(false);
        assertThrows(TooManyRequestsException.class, () -> libraryAnalysisService.createPlaylistBattle(1, 2, null, null));
    }

}