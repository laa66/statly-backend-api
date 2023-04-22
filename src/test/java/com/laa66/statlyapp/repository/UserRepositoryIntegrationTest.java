package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({"/repositories_test_data.sql"})
class UserRepositoryIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        Optional<User> optionalUser = userRepository.findByEmail("user@mail.com");
        assertTrue(optionalUser.isPresent());
        assertEquals(1, optionalUser.get().getId());
        assertEquals("user@mail.com", optionalUser.get().getEmail());
        assertEquals(LocalDateTime.of(2023, 4, 20, 14, 56,32), optionalUser.get().getJoinDate());
    }

    @Test
    void shouldNotFindUserByEmail() {
        Optional<User> optionalUser = userRepository.findByEmail("wrong@mail.com");
        assertTrue(optionalUser.isEmpty());
    }
}