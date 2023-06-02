package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import com.laa66.statlyapp.model.ItemTopTracks;
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

    @InjectMocks
    LibraryAnalysisServiceImpl libraryAnalysisService;

    @Test
    void shouldGetTopGenresArtistsValid() {
        ItemTopArtists artist1 = new ItemTopArtists();
        artist1.setGenres(List.of("classic", "classic", "classic", "rock", "rock", "rock", "rock"));
        ItemTopArtists artist2 = new ItemTopArtists();
        artist2.setGenres(List.of("classic", "classic", "classic"));
        TopArtistsDTO artistsDTO = new TopArtistsDTO("2", List.of(artist1, artist2), "long", null);
        TopGenresDTO genresDTO = new TopGenresDTO(List.of(new Genre("classic", 60), new Genre("rock", 40)), "long", null);
        when(statsService.compareGenres(eq(1L), any())).thenReturn(genresDTO);
        TopGenresDTO returnDto = libraryAnalysisService.getTopGenres(1, "long", artistsDTO);

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
                () -> libraryAnalysisService.getTopGenres(1, "long", new TopArtistsDTO("0", null, "long", LocalDate.now())));
    }

    @Test
    void shouldGetMainstreamScoreTracksValid() {
        ItemTopTracks track1 = new ItemTopTracks();
        track1.setPopularity(40);
        ItemTopTracks track2 = new ItemTopTracks();
        track2.setPopularity(70);
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(track1, track2), "2", "long", null);
        double result = libraryAnalysisService.getMainstreamScore(tracksDTO);
        assertEquals(55.0, result);
    }

    @Test
    void shouldGetMainstreamScoreTrackListEmpty() {
        TopTracksDTO tracksDTO = new TopTracksDTO(List.of(), "0", "long", null);
        double result = libraryAnalysisService.getMainstreamScore(tracksDTO);
        assertEquals(0, result);
    }

    @Test
    void shouldGetMainstreamScoreTrackEmpty() {
        TopTracksDTO tracksDTO = new TopTracksDTO(null, "0", "long", null);
        assertThrows(RuntimeException.class, () -> libraryAnalysisService.getMainstreamScore(tracksDTO));
        assertThrows(RuntimeException.class, () -> libraryAnalysisService.getMainstreamScore(null));
    }

}