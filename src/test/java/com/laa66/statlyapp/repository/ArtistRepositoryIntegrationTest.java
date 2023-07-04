package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserArtist;
import com.laa66.statlyapp.entity.UserTrack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ArtistRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    ArtistRepository artistRepository;

    @Test
    void shouldFindFirstByUserIdAndRangeOrderByDataDesc() {
        Map<String, Integer> map = Map.of(
                "artist1", 1,
                "artist2", 2,
                "artist3", 3);
        Optional<UserArtist> artist = artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short");
        assertTrue(artist.isPresent());
        assertEquals(1, artist.get().getId());
        assertEquals(1, artist.get().getUserId());
        assertEquals("short", artist.get().getRange());
        assertEquals(map.size(), artist.get().getArtists().size());
        assertTrue(map.entrySet().stream().allMatch(e -> e.getValue().equals(artist.get().getArtists().get(e.getKey()))));
        assertEquals(LocalDate.of(2023, 4, 20), artist.get().getDate());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongId() {
        Optional<UserArtist> artist = artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(2, "short");
        assertTrue(artist.isEmpty());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongRange() {
        Optional<UserArtist> artist = artistRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long");
        assertTrue(artist.isEmpty());
    }

    @Test
    void shouldFindAllByUserId() {
        List<UserArtist> artists = (List<UserArtist>) artistRepository.findAllByUserId(1);
        assertEquals(2, artists.size());
        assertEquals(3, artists.get(0).getArtists().size());
        assertEquals(3, artists.get(1).getArtists().size());
        assertEquals(1, artists.get(0).getArtists().get("artist1"));
        assertEquals(3, artists.get(0).getArtists().get("artist3"));
    }

    @Test
    void shouldFindAllByUserIdWrongId() {
        Collection<UserArtist> artists = artistRepository.findAllByUserId(2);
        assertTrue(artists.isEmpty());
    }
}
