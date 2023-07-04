package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserTrack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TrackRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    TrackRepository trackRepository;

    @Test
    void shouldFindFirstByUserIdAndRangeOrderByDateDesc() {
        Map<String, Integer> map = Map.of(
                "artist_track1", 1,
                "artist_track2", 2,
                "artist_track3", 3);
        Optional<UserTrack> track = trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short");
        assertTrue(track.isPresent());
        assertEquals(1, track.get().getId());
        assertEquals(1, track.get().getUserId());
        assertEquals("short", track.get().getRange());
        assertEquals(map.size(), track.get().getTracks().size());
        assertTrue(map.entrySet().stream().allMatch(e -> e.getValue().equals(track.get().getTracks().get(e.getKey()))));
        assertEquals(LocalDate.of(2023, 4, 20), track.get().getDate());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDateDescWrongId() {
        Optional<UserTrack> track = trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(2, "short");
        assertTrue(track.isEmpty());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDateDescWrongRange() {
        Optional<UserTrack> track = trackRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long");
        assertTrue(track.isEmpty());
    }

    @Test
    void shouldFindAllByUserId() {
        List<UserTrack> tracks = (List<UserTrack>) trackRepository.findAllByUserId(1);
        assertEquals(2, tracks.size());
        assertEquals(3, tracks.get(0).getTracks().size());
        assertEquals(3, tracks.get(1).getTracks().size());
        assertEquals(1, tracks.get(0).getTracks().get("artist_track1"));
        assertEquals(3, tracks.get(0).getTracks().get("artist_track3"));
    }

    @Test
    void shouldFindAllByUserIdWrongId() {
        Collection<UserTrack> tracks = trackRepository.findAllByUserId(3);
        assertTrue(tracks.isEmpty());
    }

}