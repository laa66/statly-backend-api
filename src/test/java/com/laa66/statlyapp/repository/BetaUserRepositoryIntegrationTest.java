package com.laa66.statlyapp.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class BetaUserRepositoryIntegrationTest extends MySQLBaseContainerTest {

    @Autowired
    BetaUserRepository betaUserRepository;

    @Test
    void shouldExistsByEmailExist() {
        boolean exists = betaUserRepository.existsByEmail("test1@mail.com");
        assertTrue(exists);
    }

    @Test
    void shouldExistsByEmailNotExists() {
        boolean exists = betaUserRepository.existsByEmail("test3@mail.com");
        assertFalse(exists);
    }

}
