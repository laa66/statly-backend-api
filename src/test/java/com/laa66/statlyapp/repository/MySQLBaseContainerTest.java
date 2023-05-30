package com.laa66.statlyapp.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

@SpringBootTest
public class MySQLBaseContainerTest {

    @Autowired
    protected DataSource dataSource;

    protected final static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0.31")
            .withDatabaseName("test")
            .withUsername("username")
            .withPassword("password")
            .withInitScript("repositories_test_data.sql");

    static {
        mySQLContainer.start();
    }

    @DynamicPropertySource
    private static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }
}
