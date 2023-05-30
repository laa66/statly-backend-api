package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserGenre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class GenreRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    GenreRepository genreRepository;

    @Test
    void shouldFindFirstByUserIdAndRangeOrderByDataDesc() {
        Map<String, Integer> map = Map.of(
                "genre1", 20,
                "genre2", 25,
                "genre3", 35);
        Optional<UserGenre> genre = genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short");
        assertTrue(genre.isPresent());
        assertEquals(1, genre.get().getId());
        assertEquals(1, genre.get().getUserId());
        assertEquals("short", genre.get().getRange());
        assertEquals(map.size(), genre.get().getGenres().size());
        assertTrue(map.entrySet().stream().allMatch(e -> e.getValue().equals(genre.get().getGenres().get(e.getKey()))));
        assertEquals(LocalDate.of(2023, 4, 20), genre.get().getDate());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongId() {
        Optional<UserGenre> genre = genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(2, "short");
        assertTrue(genre.isEmpty());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongRange() {
        Optional<UserGenre> genre = genreRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long");
        assertTrue(genre.isEmpty());
    }
}
