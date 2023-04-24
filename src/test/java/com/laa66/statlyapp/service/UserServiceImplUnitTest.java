package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.MainstreamScoreDTO;
import com.laa66.statlyapp.DTO.TopArtistsDTO;
import com.laa66.statlyapp.DTO.TopGenresDTO;
import com.laa66.statlyapp.DTO.TopTracksDTO;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    TrackRepository trackRepository;

    @Mock
    ArtistRepository artistRepository;

    @Mock
    GenreRepository genreRepository;

    @Mock
    MainstreamRepository mainstreamRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void shouldFindUserByEmail() {
        User user = new User(1, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        Optional<User> returnUser = userRepository.findByEmail("user@mail.com");
        assertTrue(returnUser.isPresent());
        assertEquals(user.getId(), returnUser.get().getId());
        assertEquals(user.getEmail(), returnUser.get().getEmail());
        assertEquals(user.getJoinDate(), returnUser.get().getJoinDate());
    }

    @Test
    void shouldNotFindUserByEmail() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());
        Optional<User> returnUser = userRepository.findByEmail("user@mail.com");
        assertTrue(returnUser.isEmpty());
    }

    @Test
    void shouldSaveUser() {
        User beforeSaveUser = new User(0, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        User afterSaveUser = new User(1, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.save(beforeSaveUser)).thenReturn(afterSaveUser);
        User returnUser = userService.saveUser(beforeSaveUser);
        assertNotNull(returnUser);
        assertNotEquals(beforeSaveUser.getId(), returnUser.getId());
        assertEquals(afterSaveUser.getId(), returnUser.getId());
        assertEquals(beforeSaveUser.getEmail(), returnUser.getEmail());
        assertEquals(beforeSaveUser.getJoinDate(), returnUser.getJoinDate());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(1L, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteUserThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void shouldSaveUserTracks() {
        ItemTopTracks item = new ItemTopTracks();
        item.setArtists(Collections.singletonList(new Artist("artist")));
        item.setName("song");
        TopTracksDTO dto = new TopTracksDTO(Collections.singletonList(item), "1", "short");
        Map<TopTracksDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        userService.saveUserTracks(dtoMap);
        verify(trackRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserArtists() {
        ItemTopArtists item = new ItemTopArtists();
        item.setName("artist");
        TopArtistsDTO dto = new TopArtistsDTO("1", Collections.singletonList(item), "short");
        Map<TopArtistsDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        userService.saveUserArtists(dtoMap);
        verify(artistRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserGenres() {
        TopGenresDTO dto = new TopGenresDTO(Collections
                .singletonList(new Genre("genre", 20)), "short");
        Map<TopGenresDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        userService.saveUserGenres(dtoMap);
        verify(genreRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldSaveUserMainstream() {
        MainstreamScoreDTO dto = new MainstreamScoreDTO(40.00, "short");
        Map<MainstreamScoreDTO, Long> dtoMap = Collections.singletonMap(dto, 1L);
        userService.saveUserMainstream(dtoMap);
        verify(mainstreamRepository, times(1)).saveAll(anyList());
    }

    @Test
    void shouldCompareTracks() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "short");
        UserTrack track = new UserTrack(1, 1, "short", Map.of(
                "artist1_track1", 2, "artist2_track2", 1
        ), LocalDate.now());
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(track));

        TopTracksDTO returnDto = userService.compareTracks(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(1, returnDto.getItemTopTracks().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getItemTopTracks().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
    }

    @Test
    void shouldNotCompareTracksWrongId() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "short");
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());
        TopTracksDTO returnDto = userService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(dto.getItemTopTracks().get(0).getDifference(), returnDto.getItemTopTracks().get(0).getDifference());
        assertEquals(dto.getItemTopTracks().get(1).getDifference(), returnDto.getItemTopTracks().get(1).getDifference());
    }

    @Test
    void shouldNotCompareTracksWrongRangeInDto() {
        TopTracksDTO dto = new TopTracksDTO(List.of(
                new ItemTopTracks(new Album(), List.of(new Artist("artist1")), "track1", 50, "uri", new SpotifyURL(), 0),
                new ItemTopTracks(new Album(), List.of(new Artist("artist2")), "track2", 50, "uri", new SpotifyURL(), 0)
        ), "2", "wrong");
        when(trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());
        TopTracksDTO returnDto = userService.compareTracks(1, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopTracks().size(), returnDto.getItemTopTracks().size());
        assertEquals(dto.getItemTopTracks().get(0).getName(), returnDto.getItemTopTracks().get(0).getName());
        assertEquals(dto.getItemTopTracks().get(1).getName(), returnDto.getItemTopTracks().get(1).getName());
        assertEquals(dto.getItemTopTracks().get(0).getDifference(), returnDto.getItemTopTracks().get(0).getDifference());
        assertEquals(dto.getItemTopTracks().get(1).getDifference(), returnDto.getItemTopTracks().get(1).getDifference());
    }

    @Test
    void shouldCompareArtists() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL()),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL())
        ), "short");
        UserArtist artist = new UserArtist(1, 1, "short", Map.of(
                "artist1", 2, "artist2", 1
        ), LocalDate.now());
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(artist));

        TopArtistsDTO returnDto = userService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(1, returnDto.getItemTopArtists().get(0).getDifference(), "Artist1 was on second place and now should be at first place");
        assertEquals(-1, returnDto.getItemTopArtists().get(1).getDifference(), "Artist2 was on first place and now should be at second place");
    }

    @Test
    void shouldNotCompareArtistsWrongId() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL()),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL())
        ), "short");
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        TopArtistsDTO returnDto = userService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(dto.getItemTopArtists().get(0).getDifference(), returnDto.getItemTopArtists().get(0).getDifference());
        assertEquals(dto.getItemTopArtists().get(1).getDifference(), returnDto.getItemTopArtists().get(1).getDifference());
    }

    @Test
    void shouldNotCompareArtistsWrongRange() {
        TopArtistsDTO dto = new TopArtistsDTO("2", List.of(
                new ItemTopArtists(List.of(), List.of(), "artist1", "uri", new SpotifyURL()),
                new ItemTopArtists(List.of(), List.of(), "artist2", "uri", new SpotifyURL())
        ), "wrong");
        when(artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        TopArtistsDTO returnDto = userService.compareArtists(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getTotal(), returnDto.getTotal());
        assertEquals(dto.getItemTopArtists().size(), returnDto.getItemTopArtists().size());
        assertEquals(dto.getItemTopArtists().get(0).getName(), returnDto.getItemTopArtists().get(0).getName());
        assertEquals(dto.getItemTopArtists().get(1).getName(), returnDto.getItemTopArtists().get(1).getName());
        assertEquals(dto.getItemTopArtists().get(0).getDifference(), returnDto.getItemTopArtists().get(0).getDifference());
        assertEquals(dto.getItemTopArtists().get(1).getDifference(), returnDto.getItemTopArtists().get(1).getDifference());
    }

    @Test
    void shouldCompareGenres() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short");

        UserGenre genre = new UserGenre(1, 1, "short", Map.of(
                "genre1", 10, "genre2", 30, "genre4", 5
        ), LocalDate.now());
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(genre));

        TopGenresDTO returnDto = userService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(10, returnDto.getGenres().get(0).getDifference());
        assertEquals(-15, returnDto.getGenres().get(1).getDifference());
        assertEquals(8, returnDto.getGenres().get(2).getDifference());
    }

    @Test
    void shouldCompareGenresWrongId() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "short");
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        TopGenresDTO returnDto = userService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(dto.getGenres().get(0).getDifference(), returnDto.getGenres().get(0).getDifference());
        assertEquals(dto.getGenres().get(1).getDifference(), returnDto.getGenres().get(1).getDifference());
        assertEquals(dto.getGenres().get(2).getDifference(), returnDto.getGenres().get(2).getDifference());
    }

    @Test
    void shouldCompareGenresWrongRange() {
        TopGenresDTO dto = new TopGenresDTO(List.of(
                new Genre("genre1", 20),
                new Genre("genre2", 15),
                new Genre("genre3", 8)
        ), "wrong");
        when(genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        TopGenresDTO returnDto = userService.compareGenres(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getGenres().get(0).getGenre(), returnDto.getGenres().get(0).getGenre());
        assertEquals(dto.getGenres().get(1).getGenre(), returnDto.getGenres().get(1).getGenre());
        assertEquals(dto.getGenres().get(2).getGenre(), returnDto.getGenres().get(2).getGenre());
        assertEquals(dto.getGenres().get(0).getDifference(), returnDto.getGenres().get(0).getDifference());
        assertEquals(dto.getGenres().get(1).getDifference(), returnDto.getGenres().get(1).getDifference());
        assertEquals(dto.getGenres().get(2).getDifference(), returnDto.getGenres().get(2).getDifference());
    }

    @Test
    void shouldCompareMainstream() {
        MainstreamScoreDTO dto = new MainstreamScoreDTO(60.50, "short");
        UserMainstream mainstream = new UserMainstream(1, 1, "short", LocalDate.now(), 30.76);
        when(mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.of(mainstream));

        MainstreamScoreDTO returnDto = userService.compareMainstream(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getScore(), returnDto.getScore());
        assertEquals(29.74, returnDto.getDifference());
    }

    @Test
    void shouldCompareMainstreamWrongId() {
        MainstreamScoreDTO dto = new MainstreamScoreDTO(60.50, "short");
        when(mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short"))
                .thenReturn(Optional.empty());

        MainstreamScoreDTO returnDto = userService.compareMainstream(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getScore(), returnDto.getScore());
        assertEquals(dto.getDifference(), returnDto.getDifference());
    }

    @Test
    void shouldCompareMainstreamWrongRange() {
        MainstreamScoreDTO dto = new MainstreamScoreDTO(60.50, "wrong");
        when(mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "wrong"))
                .thenReturn(Optional.empty());

        MainstreamScoreDTO returnDto = userService.compareMainstream(1L, dto);
        assertNotNull(returnDto);
        assertEquals(dto.getRange(), returnDto.getRange());
        assertEquals(dto.getScore(), returnDto.getScore());
        assertEquals(dto.getDifference(), returnDto.getDifference());
    }
}