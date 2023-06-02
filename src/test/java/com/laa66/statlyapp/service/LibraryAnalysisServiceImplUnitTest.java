package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIEmptyResponseException;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.ItemTopArtists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

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
    void shouldGetTopGenresWithValidUrl() {
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
}