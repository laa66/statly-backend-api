package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.UserMainstream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MainstreamRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    MainstreamRepository mainstreamRepository;

    @Test
    void shouldFindFirstByUserIdAndRangeOrderByDataDesc() {
        Optional<UserMainstream> mainstream = mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "short");
        assertTrue(mainstream.isPresent());
        assertEquals(1, mainstream.get().getId());
        assertEquals(1, mainstream.get().getUserId());
        assertEquals("short", mainstream.get().getRange());
        assertEquals(50.50, mainstream.get().getScore());
        assertEquals(LocalDate.of(2023, 4, 20), mainstream.get().getDate());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongId() {
        Optional<UserMainstream> mainstream = mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(2, "short");
        assertTrue(mainstream.isEmpty());
    }

    @Test
    void shouldNotFindFirstByUserIdAndRangeOrderByDataDescWrongRange() {
        Optional<UserMainstream> mainstream = mainstreamRepository.findFirstByUserIdAndRangeOrderByDateDesc(1, "long");
        assertTrue(mainstream.isEmpty());
    }

}