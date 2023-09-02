package com.laa66.statlyapp.repository;

import com.laa66.statlyapp.entity.BetaUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BetaUserRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    BetaUserRepository betaUserRepository;

    @Test
    void shouldExistsByEmailExists() {
        boolean exists = betaUserRepository.existsByEmail("test1@mail.com");
        assertTrue(exists);
    }

    @Test
    void shouldExistsByEmailNotExists() {
        boolean exists = betaUserRepository.existsByEmail("test3@mail.com");
        assertFalse(exists);
    }

    @Test
    void shouldFindByEmailExists() {
        Optional<BetaUser> betaUser = betaUserRepository.findByEmail("test1@mail.com");
        assertTrue(betaUser.isPresent());
        assertEquals(1, betaUser.get().getId());
        assertEquals("Random Name", betaUser.get().getFullName());
        assertEquals("test1@mail.com", betaUser.get().getEmail());
        assertNotNull(betaUser.get().getDate());
    }

    @Test
    void shouldFindByEmailNotExists() {
        Optional<BetaUser> betaUser = betaUserRepository.findByEmail("test3@mail.com");
        assertFalse(betaUser.isPresent());
    }

}
