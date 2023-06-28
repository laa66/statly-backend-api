package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldFindByEmail() {
        Optional<User> optionalUser = userRepository.findByEmail("user@mail.com");
        assertTrue(optionalUser.isPresent());
        assertEquals(1, optionalUser.get().getId());
        assertEquals("user@mail.com", optionalUser.get().getEmail());
        assertEquals(LocalDateTime.of(2023, 4, 20, 14, 56,32), optionalUser.get().getJoinDate());
        assertEquals(50.0, optionalUser.get().getUserStats().getEnergy());
        assertEquals(500, optionalUser.get().getUserStats().getPoints());

        Optional<User> emptyUser = userRepository.findByEmail("wrong@mail.com");
        assertTrue(emptyUser.isEmpty());
    }

    @Test
    void shouldFindByUsername() {
        Optional<User> optionalUser = userRepository.findByUsername("username");
        assertTrue(optionalUser.isPresent());
        assertEquals(1, optionalUser.get().getId());
        assertEquals("user@mail.com", optionalUser.get().getEmail());
        assertEquals(LocalDateTime.of(2023, 4, 20, 14, 56,32), optionalUser.get().getJoinDate());
        assertEquals(50.0, optionalUser.get().getUserStats().getEnergy());
        assertEquals(500, optionalUser.get().getUserStats().getPoints());

        Optional<User> emptyUser = userRepository.findByUsername("wrongUsername");
        assertTrue(emptyUser.isEmpty());
    }
}