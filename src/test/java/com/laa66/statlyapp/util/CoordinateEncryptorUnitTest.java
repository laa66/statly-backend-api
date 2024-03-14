package com.laa66.statlyapp.util;

import com.laa66.statlyapp.model.mapbox.Coordinates;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateEncryptorUnitTest {

    @Test
    void shouldEncryptCoordinatesPositiveBoth() {
        Coordinates coordinates = new Coordinates(53.231, 43.21);

        Pair<String, String> encryptedCoordinates = CoordinateEncryptor.encrypt(coordinates);

        assertEquals("1213235", encryptedCoordinates.getFirst());
        assertEquals("121234", encryptedCoordinates.getSecond());
    }

    @Test
    void shouldEncryptCoordinatesNegativeFirst() {
        Coordinates coordinates = new Coordinates(-53.231, 43.21);

        Pair<String, String> encryptedCoordinates = CoordinateEncryptor.encrypt(coordinates);

        assertEquals("0313235", encryptedCoordinates.getFirst());
        assertEquals("121234", encryptedCoordinates.getSecond());
    }

    @Test
    void shouldEncryptCoordinatesNegativeSecond() {
        Coordinates coordinates = new Coordinates(53.231, -43.21);

        Pair<String, String> encryptedCoordinates = CoordinateEncryptor.encrypt(coordinates);

        assertEquals("1213235", encryptedCoordinates.getFirst());
        assertEquals("031234", encryptedCoordinates.getSecond());
    }

    @Test
    void shouldEncryptCoordinatesNegativeBoth() {
        Coordinates coordinates = new Coordinates(-53.231, -43.21);

        Pair<String, String> encryptedCoordinates = CoordinateEncryptor.encrypt(coordinates);

        assertEquals("0313235", encryptedCoordinates.getFirst());
        assertEquals("031234", encryptedCoordinates.getSecond());
    }



}
