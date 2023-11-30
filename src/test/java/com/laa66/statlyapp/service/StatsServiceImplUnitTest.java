package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.spotify.*;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    TrackRepository trackRepository;

    @Mock
    ArtistRepository artistRepository;

    @Mock
    GenreRepository genreRepository;

    @InjectMocks
    StatsServiceImpl statsService;

    @Test
    void shouldGetUserTracksValidUserId() {
        UserTrack userTrack = new UserTrack(1, 1, "long", Map.of(
                "artist1_title1", 1, "artist2_title2", 2
        ), LocalDate.of(2023, 1, 1));
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long"))
                .thenReturn(Optional.of(userTrack));
        TracksDTO tracksDTO = statsService.getUserTracks(1, "long");
        assertEquals("artist1", tracksDTO.getTracks().get(0).getArtists().get(0).getName());
        assertEquals("title1", tracksDTO.getTracks().get(0).getName());
        assertEquals("artist2", tracksDTO.getTracks().get(1).getArtists().get(0).getName());
        assertEquals("title2", tracksDTO.getTracks().get(1).getName());
    }

    @Test
    void shouldGetUserTracksNotValidUserId() {
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long"))
                .thenReturn(Optional.empty());
        TracksDTO tracksDTO = statsService.getUserTracks(1, "long");
        assertNull(tracksDTO.getTracks());
        assertEquals("0", tracksDTO.getTotal());
        assertEquals("long", tracksDTO.getRange());
        assertNull(tracksDTO.getDate());
    }

    @Test
    void shouldGetUserArtistsValidUserId() {
        UserArtist userArtist = new UserArtist(1, 1, "long", Map.of(
                "artist2", 2, "artist1", 1
        ), LocalDate.of(2023, 1, 1));
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long"))
                .thenReturn(Optional.of(userArtist));
        ArtistsDTO artistsDTO = statsService.getUserArtists(1, "long");
        assertEquals("artist1", artistsDTO.getArtists().get(0).getName());
        assertEquals("artist2", artistsDTO.getArtists().get(1).getName());
    }

    @Test
    void shouldGetUserArtistsNotValidUserId() {
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long"))
                .thenReturn(Optional.empty());
        ArtistsDTO artistsDTO = statsService.getUserArtists(1, "long");
        assertEquals("0", artistsDTO.getTotal());
        assertNull(artistsDTO.getArtists());
        assertEquals("long", artistsDTO.getRange());
        assertNull(artistsDTO.getDate());
    }

    @Test
    void shouldSaveUserTracks() {
        Track track = new Track(List.of(new Artist("artist")), "song");
        TracksDTO trackDTO = new TracksDTO(Collections.singletonList(track), "1", "short", null, null);
        Map<TracksDTO, Long> trackMap = Collections.singletonMap(trackDTO, 1L);
        statsService.saveUserTracks(trackMap);

        Track track1 = new Track(Collections.singletonList(new Artist("artist")), "song");
        Track track2 = new Track(Collections.singletonList(new Artist("artist")), "song");
        TracksDTO tracksDTO = new TracksDTO(List.of(track1, track2), "2", "short", null, null);
        Map<TracksDTO, Long> tracksMap = Map.of(tracksDTO, 1L);
        statsService.saveUserTracks(tracksMap);
        verify(trackRepository, times(2)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserArtists() {
        Artist artist = new Artist("artist");
        ArtistsDTO artistDTO = new ArtistsDTO("1", Collections.singletonList(artist), "short", null, null);
        Map<ArtistsDTO, Long> artistMap = Collections.singletonMap(artistDTO, 1L);
        statsService.saveUserArtists(artistMap);

        Artist artist1 = new Artist("artist");
        Artist artist2 = new Artist("artist");
        ArtistsDTO artistsDTO = new ArtistsDTO("2", List.of(artist1, artist2), "short", null, null);
        Map<ArtistsDTO, Long> artistsMap = Map.of(artistsDTO, 1L);
        statsService.saveUserArtists(artistsMap);
        verify(artistRepository, times(2)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserGenres() {
        GenresDTO genreDTO = new GenresDTO(Collections
                .singletonList(new Genre("genre", 20)), "short", null, null);
        Map<GenresDTO, Long> genreMap = Collections.singletonMap(genreDTO, 1L);
        statsService.saveUserGenres(genreMap);
        verify(genreRepository, times(1)).saveAll(anyList());

        Genre genre1 = new Genre("rock", 30);
        Genre genre2 = new Genre("rock", 60);
        GenresDTO genresDTO = new GenresDTO(List.of(genre1, genre2), "short", null, null);
        statsService.saveUserGenres(Map.of(genresDTO, 1L));
        verify(genreRepository, times(2)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserStats() {
        User user = new User()
                .withId(1L)
                .withUserStats(new UserStats(1L, 0., 0., 0., 0., 0, 0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Map<String, Double> statsMap = Map.of(
                "energy", 50.0,
                "tempo", 127.0,
                "mainstream", 61.0,
                "boringness", 300.0
        );
        statsService.saveUserStats(1L, statsMap);
        verify(userRepository, times(1)).save(argThat(argument ->
                argument.getUserStats().getBoringness() == statsMap.get("boringness") &&
                argument.getUserStats().getMainstream() == statsMap.get("mainstream") &&
                argument.getUserStats().getEnergy() == statsMap.get("energy") &&
                argument.getUserStats().getTempo() == statsMap.get("tempo")));

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> statsService.saveUserStats(2L, statsMap));
    }

    @Test
    void shouldCompareTracks() {
        Artist artist1 = new Artist("artist1");
        Artist artist2 = new Artist("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null, null);
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
        assertEquals(dto.getDate(), returnDto.getDate());
    }

    @Test
    void shouldCompareTracksNullAndEmptyArtists() {
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), null, "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null, null);
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
        assertEquals(dto.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareTracksWrongId() {
        Artist artist1 = new Artist("artist1");
        Artist artist2 = new Artist("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "short", null, null);
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
        Artist artist1 = new Artist("artist1");
        Artist artist2 = new Artist("artist2");
        TracksDTO dto = new TracksDTO(List.of(
                new Track(new Album(), List.of(artist1), "track1", 50, "uri", new SpotifyURL(), "id", 0),
                new Track(new Album(), List.of(artist2), "track2", 50, "uri", new SpotifyURL(), "id", 0)
        ), "2", "wrong", null, null);
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
        ), "short", null, null);
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
        assertEquals(dto.getDate(), returnDto.getDate());
    }

    @Test
    void shouldNotCompareArtistsWrongId() {
        ArtistsDTO dto = new ArtistsDTO("2", List.of(
                new Artist(List.of(), List.of(), "artist1", "uri", new SpotifyURL(), 0),
                new Artist(List.of(), List.of(), "artist2", "uri", new SpotifyURL(), 0)
        ), "short", null, null);
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
        ), "wrong", null, null);
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
        ), "short", null, null);

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
        assertEquals(dto.getDate(), returnDto.getDate());
    }

    @Test
    void shouldCompareGenresWrongId() {
        GenresDTO dto = new GenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short", null, null);
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
        ), "wrong", null, null);
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

    @Test
    void shouldMatchTracks() {
        Collection<UserTrack> tracks = List.of(
                new UserTrack(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserTrack(2, 1, "short", Map.of(
                        "item3", 1, "item4", 2, "item5", 3
                ), null)
        );
        Collection<UserTrack> matchTracks = List.of(
                new UserTrack(3, 2, "long", Map.of(
                        "item1", 1, "item3", 3
                ), null),
                new UserTrack(4, 2, "short", Map.of(
                        "item3", 1, "item1", 3
                ), null)
        );
        when(trackRepository.findAllByUserId(1)).thenReturn(tracks);
        when(trackRepository.findAllByUserId(2)).thenReturn(matchTracks);

        Pair<Integer, Integer> pair = statsService.matchTracks(1, 2);
        assertEquals(2, pair.getFirst());
        assertEquals(5, pair.getSecond());
    }

    @Test
    void shouldMatchTracksWrongUserId() {
        Collection<UserTrack> matchTracks = List.of(
                new UserTrack(3, 2, "long", Map.of(
                        "item1", 1, "item3", 3
                ), null),
                new UserTrack(4, 2, "short", Map.of(
                        "item3", 1, "item1", 3
                ), null)
        );
        when(trackRepository.findAllByUserId(1)).thenReturn(Collections.emptyList());
        when(trackRepository.findAllByUserId(2)).thenReturn(matchTracks);
        Pair<Integer, Integer> pair = statsService.matchTracks(1, 2);
        assertEquals(0, pair.getFirst());
        assertEquals(0, pair.getSecond());
    }

    @Test
    void shouldMatchTracksWrongMatchUserId() {
        Collection<UserTrack> tracks = List.of(
                new UserTrack(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserTrack(2, 1, "short", Map.of(
                        "item3", 1, "item4", 2, "item5", 3
                ), null)
        );
        when(trackRepository.findAllByUserId(1)).thenReturn(tracks);
        when(trackRepository.findAllByUserId(2)).thenReturn(Collections.emptyList());
        Pair<Integer, Integer> pair = statsService.matchTracks(1, 2);
        assertEquals(0, pair.getFirst());
        assertEquals(5, pair.getSecond());
    }

    @Test
    void shouldMatchArtists() {
        Collection<UserArtist> artists = List.of(
                new UserArtist(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(2, 1, "short", Map.of(
                        "item1", 1, "item3", 2, "item4", 3
                ), null)
        );
        Collection<UserArtist> matchArtists = List.of(
                new UserArtist(3, 2, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(4, 2, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null)
        );
        when(artistRepository.findAllByUserId(1)).thenReturn(artists);
        when(artistRepository.findAllByUserId(2)).thenReturn(matchArtists);

        Pair<Integer, Integer> pair = statsService.matchArtists(1, 2);
        assertEquals(3, pair.getFirst());
        assertEquals(4, pair.getSecond());
    }

    @Test
    void shouldMatchArtistsWrongUserId() {
        Collection<UserArtist> matchArtists = List.of(
                new UserArtist(3, 2, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(4, 2, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null)
        );
        when(artistRepository.findAllByUserId(1)).thenReturn(Collections.emptyList());
        when(artistRepository.findAllByUserId(2)).thenReturn(matchArtists);
        Pair<Integer, Integer> pair = statsService.matchArtists(1, 2);
        assertEquals(0, pair.getFirst());
        assertEquals(0, pair.getSecond());
    }

    @Test
    void shouldMatchArtistsWrongMatchUserId() {
        Collection<UserArtist> artists = List.of(
                new UserArtist(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(2, 1, "short", Map.of(
                        "item1", 1, "item3", 2, "item4", 3
                ), null)
        );
        when(artistRepository.findAllByUserId(1)).thenReturn(artists);
        when(artistRepository.findAllByUserId(2)).thenReturn(Collections.emptyList());
        Pair<Integer, Integer> pair = statsService.matchArtists(1,2);
        assertEquals(0, pair.getFirst());
        assertEquals(4, pair.getSecond());
    }

    @Test
    void shouldMatchGenres() {
        Collection<UserGenre> genres = List.of(
                new UserGenre(1, 1, "long", Map.of(
                        "item1", 3, "item2", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "short", Map.of(
                        "item4", 3, "item2", 4, "item5", 5
                ), null)
        );
        Collection<UserGenre> matchGenres = List.of(
                new UserGenre(1, 1, "long", Map.of(
                        "item1", 3, "item5", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "short", Map.of(
                        "item4", 3, "item1", 4, "item3", 5
                ), null)
        );
        when(genreRepository.findAllByUserId(1)).thenReturn(genres);
        when(genreRepository.findAllByUserId(2)).thenReturn(matchGenres);
        Pair<Integer, Integer> pair = statsService.matchGenres(1, 2);
        assertEquals(4, pair.getFirst());
        assertEquals(5, pair.getSecond());
    }

    @Test
    void shouldMatchGenresWrongUserId() {
        Collection<UserGenre> matchGenres = List.of(
                new UserGenre(1, 1, "long", Map.of(
                        "item1", 3, "item5", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "short", Map.of(
                        "item4", 3, "item1", 4, "item3", 5
                ), null)
        );
        when(genreRepository.findAllByUserId(1)).thenReturn(Collections.emptyList());
        when(genreRepository.findAllByUserId(2)).thenReturn(matchGenres);
        Pair<Integer, Integer> pair = statsService.matchGenres(1, 2);
        assertEquals(0, pair.getFirst());
        assertEquals(0, pair.getSecond());
    }

    @Test
    void shouldMatchGenresWrongMatchUserId() {
        Collection<UserGenre> genres = List.of(
                new UserGenre(1, 1, "long", Map.of(
                        "item1", 3, "item2", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "short", Map.of(
                        "item4", 3, "item2", 4, "item5", 5
                ), null)
        );
        when(genreRepository.findAllByUserId(1)).thenReturn(genres);
        when(genreRepository.findAllByUserId(2)).thenReturn(Collections.emptyList());
        Pair<Integer, Integer> pair = statsService.matchGenres(1, 2);
        assertEquals(0, pair.getFirst());
        assertEquals(5, pair.getSecond());
    }

    @Test
    void shouldIsBattlePossibleTrue() {
        User user1 = new User()
                .withId(1L)
                .withUserStats(new UserStats(1, 0., 0., 0., 0., 0 ,0));
        User user2 = new User()
                .withId(2L)
                .withUserStats(new UserStats(2, 0., 0., 0., 0., 0,9));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        assertTrue(statsService.isBattlePossible(1, 2));
    }

    @Test
    void shouldIsBattlePossibleFalse() {
        User user1 = new User()
                .withId(1L)
                .withUserStats(new UserStats(1, 0., 0., 0., 0., 0 ,11));
        User user2 = new User()
                .withId(2L)
                .withUserStats(new UserStats(2, 0., 0., 0., 0., 0,9));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        assertFalse(statsService.isBattlePossible(1, 2));
    }

    @Test
    void shouldIsBattlePossibleEmptyUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(statsService.isBattlePossible(1, 2));
    }

    @Test
    void shouldIsTrackSynchronizedTrue() {
        Collection<UserTrack> tracks = List.of(
                new UserTrack(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserTrack(2, 1, "short", Map.of(
                        "item3", 1, "item4", 2, "item5", 3
                ), null)
        );
        when(trackRepository.findAllByUserId(1L)).thenReturn(tracks);
        boolean trackSynchronized = statsService.isTrackSynchronized(1L);
        assertTrue(trackSynchronized);
    }

    @Test
    void shouldIsTrackSynchronizedFalse() {
        Collection<UserTrack> tracks = List.of(
                new UserTrack(1, 1, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserTrack(2, 1, "short", Map.of(
                        "item3", 1, "item4", 2, "item5", 3
                ), null)
        );
        when(trackRepository.findAllByUserId(1L)).thenReturn(tracks);
        boolean trackSynchronized = statsService.isTrackSynchronized(1L);
        assertFalse(trackSynchronized);
    }

    @Test
    void shouldIsArtistSynchronizedTrue() {
        Collection<UserArtist> artists = List.of(
                new UserArtist(1, 1, "long", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(2, 1, "short", Map.of(
                        "item1", 1, "item3", 2, "item4", 3
                ), null)
        );
        when(artistRepository.findAllByUserId(1L)).thenReturn(artists);
        boolean artistSynchronized = statsService.isArtistSynchronized(1L);
        assertTrue(artistSynchronized);
    }

    @Test
    void shouldIsArtistSynchronizedFalse() {
        Collection<UserArtist> artists = List.of(
                new UserArtist(1, 1, "short", Map.of(
                        "item1", 1, "item2", 2, "item3", 3
                ), null),
                new UserArtist(2, 1, "short", Map.of(
                        "item1", 1, "item3", 2, "item4", 3
                ), null)
        );
        when(artistRepository.findAllByUserId(1L)).thenReturn(artists);
        boolean artistSynchronized = statsService.isArtistSynchronized(1L);
        assertFalse(artistSynchronized);
    }

    @Test
    void shouldIsGenreSynchronizedTrue() {
        Collection<UserGenre> matchGenres = List.of(
                new UserGenre(1, 1, "long", Map.of(
                        "item1", 3, "item5", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "long", Map.of(
                        "item4", 3, "item1", 4, "item3", 5
                ), null)
        );
        when(genreRepository.findAllByUserId(1L)).thenReturn(matchGenres);
        boolean genreSynchronized = statsService.isGenreSynchronized(1L);
        assertTrue(genreSynchronized);
    }

    @Test
    void shouldIsGenreSynchronizedFalse() {
        Collection<UserGenre> matchGenres = List.of(
                new UserGenre(1, 1, "medium", Map.of(
                        "item1", 3, "item5", 4, "item3", 5
                ), null),
                new UserGenre(1, 1, "short", Map.of(
                        "item4", 3, "item1", 4, "item3", 5
                ), null)
        );
        when(genreRepository.findAllByUserId(1L)).thenReturn(matchGenres);
        boolean genreSynchronized = statsService.isGenreSynchronized(1L);
        assertFalse(genreSynchronized);
    }

}
