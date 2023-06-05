package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.impl.LibraryAnalysisServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LibraryAnalysisServiceImplUnitTest {

    @Mock
    StatsService statsService;

    @Mock
    SpotifyAPIService spotifyAPIService;

    @InjectMocks
    LibraryAnalysisServiceImpl libraryAnalysisService;

    @Test
    void shouldGetTopGenresArtistsValid() {
        Artist artist1 = new Artist();
        artist1.setGenres(List.of("classic", "classic", "classic", "rock", "rock", "rock", "rock"));
        Artist artist2 = new Artist();
        artist2.setGenres(List.of("classic", "classic", "classic"));
        ArtistsDTO artistsDTO = new ArtistsDTO("2", List.of(artist1, artist2), "long", null);
        GenresDTO genresDTO = new GenresDTO(List.of(new Genre("classic", 60), new Genre("rock", 40)), "long", null);
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
                () -> libraryAnalysisService.getTopGenres(1, "long", new ArtistsDTO("0", null, "long", LocalDate.now())));
    }

    @Test
    void shouldGetLibraryAnalysis() {
        TracksDTO tracksDTO = new TracksDTO(List.of(
                new Track(new Album(), List.of(), "name", 35, "uri", new SpotifyURL(), "id1", 0),
                new Track(new Album(), List.of(), "name", 75, "uri", new SpotifyURL(), "id2", 0)
        ), "2", "long", LocalDate.now());
        ResponseTracksAnalysis tracksAnalysis = new ResponseTracksAnalysis(List.of(
                new TrackAnalysis(0.6, 0.5, 0.15, 0.23, 0.8, -6.0, 0.22, 120.0, 0.5),
                new TrackAnalysis(0.4, 0.3, 0.23, 0.13, 0.14, -45.0, 0.52, 150.5, 0.98),
                new TrackAnalysis(0.5, 0.1, 0.2, 0.3, 0.7, 0, 0.24, 98, 0.5)
        ));
        when(spotifyAPIService.getTracksAnalysis("id1,id2"))
                .thenReturn(tracksAnalysis);
        LibraryAnalysisDTO analysisDTO = libraryAnalysisService.getLibraryAnalysis(tracksDTO);

        assertEquals(0.5, analysisDTO.getLibraryAnalysis().get("acousticness"));
        assertEquals(0.3, analysisDTO.getLibraryAnalysis().get("danceability"));
        assertEquals(0.19, analysisDTO.getLibraryAnalysis().get("energy"));
        assertEquals(0.22, analysisDTO.getLibraryAnalysis().get("instrumentalness"));
        assertEquals(0.55, analysisDTO.getLibraryAnalysis().get("liveness"));
        assertEquals(-17, analysisDTO.getLibraryAnalysis().get("loudness"));
        assertEquals(0.33, analysisDTO.getLibraryAnalysis().get("speechiness"));
        assertEquals(122.83, analysisDTO.getLibraryAnalysis().get("tempo"));
        assertEquals(0.66, analysisDTO.getLibraryAnalysis().get("valence"));
        assertEquals(55.00, analysisDTO.getLibraryAnalysis().get("mainstream"));
        assertEquals(237.83, analysisDTO.getLibraryAnalysis().get("boringness"));
    }

    @Test
    void shouldGetLibraryAnalysisNullParameter() {
        assertThrows(RuntimeException.class, () -> libraryAnalysisService.getLibraryAnalysis(null));
    }

}