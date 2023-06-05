package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.entity.UserArtist;
import com.laa66.statlyapp.entity.UserGenre;
import com.laa66.statlyapp.entity.UserTrack;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.impl.StatsServiceImpl;
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

    @InjectMocks
    StatsServiceImpl statsService;

    @Test
    void shouldSaveUserTracks() {
        Track item = new Track();
        Artist artist = new Artist();
        artist.setName("artist");
        item.setArtists(Collections.singletonList(artist));
        item.setName("song");
        TracksDTO dto = new TracksDTO(Collections.singletonList(item), "1", "short", null);
        Map<TracksDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserTracks(dtoMap);
        verify(trackRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserArtists() {
        Artist item = new Artist();
        item.setName("artist");
        ArtistsDTO dto = new ArtistsDTO("1", Collections.singletonList(item), "short", null);
        Map<ArtistsDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserArtists(dtoMap);
        verify(artistRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserGenres() {
        GenresDTO dto = new GenresDTO(Collections
                .singletonList(new Genre("genre", 20)), "short", null);
        Map<GenresDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        statsService.saveUserGenres(dtoMap);
        verify(genreRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldCompareTracks() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        artist1.setName("artist1");
        artist2.setName("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null);
        UserTrack track = new UserTrack(1, 1, "short", Map.of(
                "artist1_track1", 2, "artist2_track2", 1
        ), LocalDate.of(2023, 1, 1));
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(track));

        TracksDTO returnDto = statsService.compareTracks(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getTracks().size(), returnDto.getTracks().size());
        assertEquals(dto.getTracks().get(0).getName(), returnDto.getTracks().get(0).getName());
        assertEquals(dto.getTracks().get(1).getName(), returnDto.getTracks().get(1).getName());
        assertEquals(1, returnDto.getTracks().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getTracks().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
        assertEquals(track.getDate(), returnDto.getDate());
    }

    @Test
    void shouldCompareTracksNullAndEmptyArtists() {
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), null, "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null);
        UserTrack track = new UserTrack(1, 1, "short", Map.of(
                "artist1_track1", 2, "artist2_track2", 1
        ), LocalDate.of(2023, 1, 1));
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(track));

        TracksDTO returnDto = statsService.compareTracks(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getTracks().size(), returnDto.getTracks().size());
        assertEquals(dto.getTracks().get(0).getName(), returnDto.getTracks().get(0).getName());
        assertEquals(dto.getTracks().get(1).getName(), returnDto.getTracks().get(1).getName());
        assertEquals(0, returnDto.getTracks().get(0).getDifference());
        assertEquals(0, returnDto.getTracks().get(1).getDifference());
        assertEquals(track.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareTracksWrongId() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        artist1.setName("artist1");
        artist2.setName("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null);
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());
        TracksDTO returnDto = statsService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getTracks().size(), returnDto.getTracks().size());
        assertEquals(dto.getTracks().get(0).getName(), returnDto.getTracks().get(0).getName());
        assertEquals(dto.getTracks().get(1).getName(), returnDto.getTracks().get(1).getName());
        assertEquals(dto.getTracks().get(0).getDifference(), returnDto.getTracks().get(0).getDifference());
        assertEquals(dto.getTracks().get(1).getDifference(), returnDto.getTracks().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldNotCompareTracksWrongRangeInDto() {
        Artist artist1 = new Artist();
        Artist artist2 = new Artist();
        artist1.setName("artist1");
        artist2.setName("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "wrong", null);
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());
        TracksDTO returnDto = statsService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getTracks().size(), returnDto.getTracks().size());
        assertEquals(dto.getTracks().get(0).getName(), returnDto.getTracks().get(0).getName());
        assertEquals(dto.getTracks().get(1).getName(), returnDto.getTracks().get(1).getName());
        assertEquals(dto.getTracks().get(0).getDifference(), returnDto.getTracks().get(0).getDifference());
        assertEquals(dto.getTracks().get(1).getDifference(), returnDto.getTracks().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldCompareArtists() {
        ArtistsDTO dto = new ArtistsDTO("2", List.of(
                new Artist(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new Artist(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "short", null);
        UserArtist artist = new UserArtist(1, 1, "short", Map.of(
                "artist1", 2, "artist2", 1
        ), LocalDate.of(2023, 1, 1));
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(artist));

        ArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getArtists().size(), returnDto.getArtists().size());
        assertEquals(dto.getArtists().get(0).getName(), returnDto.getArtists().get(0).getName());
        assertEquals(dto.getArtists().get(1).getName(), returnDto.getArtists().get(1).getName());
        assertEquals(1, returnDto.getArtists().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getArtists().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
        assertEquals(artist.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareArtistsWrongId() {
        ArtistsDTO dto = new ArtistsDTO("2", List.of(
                new Artist(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new Artist(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "short", null);
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        ArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getArtists().size(), returnDto.getArtists().size());
        assertEquals(dto.getArtists().get(0).getName(), returnDto.getArtists().get(0).getName());
        assertEquals(dto.getArtists().get(1).getName(), returnDto.getArtists().get(1).getName());
        assertEquals(dto.getArtists().get(0).getDifference(), returnDto.getArtists().get(0).getDifference());
        assertEquals(dto.getArtists().get(1).getDifference(), returnDto.getArtists().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldNotCompareArtistsWrongRange() {
        ArtistsDTO dto = new ArtistsDTO("2", List.of(
                new Artist(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new Artist(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "wrong", null);
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        ArtistsDTO returnDto = statsService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getArtists().size(), returnDto.getArtists().size());
        assertEquals(dto.getArtists().get(0).getName(), returnDto.getArtists().get(0).getName());
        assertEquals(dto.getArtists().get(1).getName(), returnDto.getArtists().get(1).getName());
        assertEquals(dto.getArtists().get(0).getDifference(), returnDto.getArtists().get(0).getDifference());
        assertEquals(dto.getArtists().get(1).getDifference(), returnDto.getArtists().get(1).getDifference());
        assertNull(returnDto.getDate());
    }

    @Test
    void shouldCompareGenres() {
        GenresDTO dto = new GenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short", null);

        UserGenre genre = new UserGenre(1, 1, "short", Map.of(
                "genre1", 10, "genre2", 30, "genre4", 5
        ), LocalDate.of(2023, 1, 1));
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(genre));

        GenresDTO returnDto = statsService.compareGenres(1L, dto);
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
        GenresDTO dto = new GenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short", null);
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        GenresDTO returnDto = statsService.compareGenres(1L, dto);
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
        GenresDTO dto = new GenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "wrong", null);
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        GenresDTO returnDto = statsService.compareGenres(1L, dto);
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
