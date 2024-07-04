package com.laa66.statlyapp.util;

import com.laa66.statlyapp.model.mapbox.Coordinates;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.data.util.Pair;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoordinateEncryptorUnitTest {

    private static Stream<Arguments> coordinatesProvider() {
        return Stream.of(
                Arguments.of(53.231, 43.2134),
                Arguments.of(53.231, -43.2134),
                Arguments.of(-53.231, 43.2134),
                Arguments.of(-53.231, -43.2134)
        );
    }

    @ParameterizedTest
    @MethodSource("com.laa66.statlyapp.util.CoordinateEncryptorUnitTest#coordinatesProvider")
    void shouldEncryptDecryptCoordinates(double longitude, double latitude) {
        Coordinates coordinates = new Coordinates(longitude, latitude);
        Pair<String, String> encrypted = CoordinateEncryptor.encrypt(coordinates);
        Coordinates decrypted = CoordinateEncryptor.decrypt(encrypted);

        assertEquals(coordinates.getLatitude(), decrypted.getLatitude());
        assertEquals(coordinates.getLongitude(), decrypted.getLongitude());
    }



}
