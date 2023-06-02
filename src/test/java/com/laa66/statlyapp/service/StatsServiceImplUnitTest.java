package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.UserArtist;
import com.laa66.statlyapp.entity.UserGenre;
import com.laa66.statlyapp.entity.UserMainstream;
import com.laa66.statlyapp.entity.UserTrack;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceImplUnitTest {

    @Mock
    TrackRepository trackRepository;

    @Mock
    ArtistRepository artistRepository;

    @Mock
    GenreRepository genreRepository;

    @Mock
    MainstreamRepository mainstreamRepository;

    @InjectMocks
    StatsServiceImpl statsService;

    @Test
    void shouldSaveUserTracks() {
        ItemTopTracks item = new ItemTopTracks();
        item.setArtists(Collections.singletonList(new Artist("artist")));
        item.setName("song");
        TopTracksDTO dto = new TopTracksDTO(Collections.singletonList(item), "1", "short", null);
        Map<TopTracksDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserTracks(dtoMap);
        verify(trackRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserArtists() {
        ItemTopArtists item = new ItemTopArtists();
        item.setName("artist");
        TopArtistsDTO dto = new TopArtistsDTO("1", Collections.singletonList(item), "short", null);
        Map<TopArtistsDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserArtists(dtoMap);
        verify(artistRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserGenres() {
        TopGenresDTO dto = new TopGenresDTO(Collections
                .singletonList(new Genre("genre", 20)), "short", null);
        Map<TopGenresDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserGenres(dtoMap);
        verify(genreRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldCompareTracks() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "short", null);
        UserTrack track = new UserTrack(1, 1, "short", Map.of(
                "artist1_track1", 2, "artist2_track2", 1
        ), LocalDate.of(2023, 1, 1));
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(track));

        TopTracksDTO returnDto = statsService.compareTracks(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(1, returnDto.getItemTopTracks().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getItemTopTracks().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
        assertEquals(track.getDate(), returnDto.getDate());
    }

    @Test
    void shouldCompareTracksNullAndEmptyArtists() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), null, "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "short", null);
        UserTrack track = new UserTrack(1, 1, "short", Map.of(
                "artist1_track1", 2, "artist2_track2", 1
        ), LocalDate.of(2023, 1, 1));
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(track));

        TopTracksDTO returnDto = statsService.compareTracks(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(0, returnDto.getItemTopTracks().get(0).getDifference());
        assertEquals(0, returnDto.getItemTopTracks().get(1).getDifference());
        assertEquals(track.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareTracksWrongId() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "short", null);
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());
        TopTracksDTO returnDto = statsService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(dto.getItemTopTracks().get(0).getDifference(), returnDto.getItemTopTracks().get(0).getDifference());
        assertEquals(dto.getItemTopTracks().get(1).getDifference(), returnDto.getItemTopTracks().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldNotCompareTracksWrongRangeInDto() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "wrong", null);
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());
        TopTracksDTO returnDto = statsService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(dto.getItemTopTracks().get(0).getDifference(), returnDto.getItemTopTracks().get(0).getDifference());
        assertEquals(dto.getItemTopTracks().get(1).getDifference(), returnDto.getItemTopTracks().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldCompareArtists() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "short", null);
        UserArtist artist = new UserArtist(1, 1, "short", Map.of(
                "artist1", 2, "artist2", 1
        ), LocalDate.of(2023, 1, 1));
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(artist));

        TopArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(1, returnDto.getItemTopArtists().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getItemTopArtists().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
        assertEquals(artist.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareArtistsWrongId() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "short", null);
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        TopArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(dto.getItemTopArtists().get(0).getDifference(), returnDto.getItemTopArtists().get(0).getDifference());
        assertEquals(dto.getItemTopArtists().get(1).getDifference(), returnDto.getItemTopArtists().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldNotCompareArtistsWrongRange() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "wrong", null);
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        TopArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(dto.getItemTopArtists().get(0).getDifference(), returnDto.getItemTopArtists().get(0).getDifference());
        assertEquals(dto.getItemTopArtists().get(1).getDifference(), returnDto.getItemTopArtists().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldCompareGenres() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short", null);

        UserGenre genre = new UserGenre(1, 1, "short", Map.of(
                "genre1", 10, "genre2", 30, "genre4", 5
        ), LocalDate.of(2023, 1, 1));
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(genre));

        TopGenresDTO returnDto = statsService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(10, returnDto.getGenres().get(0).getDifference());
        assertEquals(-15, returnDto.getGenres().get(1).getDifference());
        assertNull(returnDto.getGenres().get(2).getDifference());
        assertEquals(genre.getDate(), returnDto.getDate());
    }

    @Test
    void shouldCompareGenresWrongId() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short", null);
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        TopGenresDTO returnDto = statsService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(dto.getGenres().get(0).getDifference(), returnDto.getGenres().get(0).getDifference());
        assertEquals(dto.getGenres().get(1).getDifference(), returnDto.getGenres().get(1).getDifference());
        assertEquals(dto.getGenres().get(2).getDifference(), returnDto.getGenres().get(2).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldCompareGenresWrongRange() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "wrong", null);
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        TopGenresDTO returnDto = statsService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(dto.getGenres().get(0).getDifference(), returnDto.getGenres().get(0).getDifference());
        assertEquals(dto.getGenres().get(1).getDifference(), returnDto.getGenres().get(1).getDifference());
        assertEquals(dto.getGenres().get(2).getDifference(), returnDto.getGenres().get(2).getDifference());
        assertNull(returnDto.getDate());
    }
}
