package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.constants.MapboxAPI;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.mapbox.Location;
import com.laa66.statlyapp.model.mapbox.Matrix;
import com.laa66.statlyapp.service.impl.MapboxMapAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MapboxMapAPIServiceUnitTest {

    @Mock
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    MapAPIService mapAPIService;

    @BeforeEach
    void setup() {
        mapAPIService = new MapboxMapAPIService("token", restTemplate);
    }

    @Test
    void shouldGetDistanceMatrixValidResponse() {
        List<UserDTO> users = List.of(
                UserDTO.builder()
                        .id("1")
                        .coordinates(new Coordinates(50.1, 1.5))
                        .build(),
                UserDTO.builder()
                        .id("2")
                        .coordinates(new Coordinates(25., 1.5))
                        .build(),
                UserDTO.builder()
                        .id("3")
                        .coordinates(new Coordinates(30.7, 1.5))
                        .build()
        );
        Matrix matrix = new Matrix("OK",
                List.of(List.of(0., 5.6, 3.0)),
                Collections.emptyList());
        when(restTemplate.getForObject(MapboxAPI.DISTANCE_MATRIX.get()
                .replace("{coordinates_list}", "50.1,1.5;25.0,1.5;30.7,1.5")
                .replace("{access_token}", "token"), Matrix.class))
                .thenReturn(matrix);
        List<UserDTO> returnedUsers = (List<UserDTO>) mapAPIService.getDistanceMatrix(users);
        assertEquals(3, returnedUsers.size());
        assertEquals(0., returnedUsers.get(0).getDistance());
        assertEquals(5.6, returnedUsers.get(1).getDistance());
        assertEquals(3.0, returnedUsers.get(2).getDistance());
    }

    @Test
    void shouldGetDistanceMatrixInvalidResponse() {
        List<UserDTO> users = List.of(
                UserDTO.builder()
                        .id("1")
                        .coordinates(new Coordinates(50.1, 1.5))
                        .build(),
                UserDTO.builder()
                        .id("2")
                        .coordinates(new Coordinates(25., 1.5))
                        .build()
        );
        when(restTemplate.getForObject(anyString(), any()))
                .thenReturn(null)
                .thenReturn(new Matrix("OK", null, null))
                .thenReturn(new Matrix("OK", List.of(), null))
                .thenReturn(new Matrix("OK", List.of(List.of()), null))
                .thenReturn(new Matrix("OK", List.of(List.of(50.3)), null));
        List<UserDTO> returnedUsers1 = (List<UserDTO>) mapAPIService.getDistanceMatrix(users);
        assertTrue(returnedUsers1.isEmpty());

        List<UserDTO> returnedUsers2 = (List<UserDTO>) mapAPIService.getDistanceMatrix(users);
        assertTrue(returnedUsers2.isEmpty());

        List<UserDTO> returnedUsers3 = (List<UserDTO>) mapAPIService.getDistanceMatrix(users);
        assertTrue(returnedUsers3.isEmpty());

        List<UserDTO> returnedUsers4 = (List<UserDTO>) mapAPIService.getDistanceMatrix(users);
        assertTrue(returnedUsers4.isEmpty());
    }

    @Test
    void shouldGetDistanceMatrixResponseError() {
        List<UserDTO> users = List.of(
                UserDTO.builder()
                        .id("1")
                        .coordinates(new Coordinates(50.1, 1.5))
                        .build(),
                UserDTO.builder()
                        .id("2")
                        .coordinates(new Coordinates(25., 1.5))
                        .build()
        );
        when(restTemplate.getForObject(MapboxAPI.DISTANCE_MATRIX.get()
                .replace("{coordinates_list}", "50.1,1.5;25.0,1.5")
                .replace("{access_token}", "token"), Matrix.class))
                .thenThrow(RestClientException.class);
        assertThrows(RestClientException.class, () -> mapAPIService.getDistanceMatrix(users));
    }

    @Test
    void shouldGetReverseGeocodingValidResponse() {
        UserDTO user = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(15.3, 17.45))
                .build();
        Location location = new Location();
        when(restTemplate.getForObject(MapboxAPI.REVERSE_GEOCODING.get()
                .replace("{coordinates_list}", "15.3,17.45")
                .replace("{access_token}", "token"), Location.class))
                .thenReturn(location);
        UserDTO returnedUser = mapAPIService.getReverseGeocoding(user);
        assertEquals(location, returnedUser.getLocation());
    }

    @Test
    void shouldGetReverseGeocodingInvalidResponse() {
        UserDTO user = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(15.3, 17.45))
                .build();
        when(restTemplate.getForObject(MapboxAPI.REVERSE_GEOCODING.get()
                .replace("{coordinates_list}", "15.3,17.45")
                .replace("{access_token}", "token"), Location.class))
                .thenReturn(null);
        UserDTO returnedUser = mapAPIService.getReverseGeocoding(user);
        assertNull(returnedUser.getLocation());
    }

    @Test
    void shouldGetReverseGeocodingResponseError() {
        UserDTO user = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(15.3, 17.45))
                .build();
        when(restTemplate.getForObject(MapboxAPI.REVERSE_GEOCODING.get()
                .replace("{coordinates_list}", "15.3,17.45")
                .replace("{access_token}", "token"), Location.class))
                .thenThrow(RestClientException.class);
        assertThrows(RestClientException.class, () -> mapAPIService.getReverseGeocoding(user));

    }
}