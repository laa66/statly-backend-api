package com.laa66.statlyapp.DTO;

import com.laa66.statlyapp.model.mapbox.Coordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOUnitTest {

    @Test
    void shouldIsNearby() {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(50.34, 16.94))
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .coordinates(new Coordinates(50.12, 16.70))
                .build();
        UserDTO user3 = UserDTO.builder()
                .id("3")
                .coordinates(new Coordinates(51.20, 16.43))
                .build();
        UserDTO user4 = UserDTO.builder()
                .id("4")
                .coordinates(new Coordinates(50.22, 16.01))
                .build();
        assertTrue(user1.isNearby(user2));
        assertTrue(user2.isNearby(user1));
        assertFalse(user1.isNearby(user3));
        assertFalse(user3.isNearby(user1));
        assertFalse(user2.isNearby(user3));
        assertFalse(user3.isNearby(user2));
        assertFalse(user2.isNearby(user4));
        assertFalse(user4.isNearby(user2));
    }

}