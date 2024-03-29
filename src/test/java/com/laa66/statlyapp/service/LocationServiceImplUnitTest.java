package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.mapbox.Location;
import com.laa66.statlyapp.service.impl.LocationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceImplUnitTest {

    @Mock
    MapAPIService mapAPIService;

    @Mock
    UserService userService;

    @Mock
    LibraryAnalysisService analysisService;

    @InjectMocks
    LocationServiceImpl locationService;

    @Test
    void shouldFindBestMatchingUsersValidId() {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(50., 54.))
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .coordinates(new Coordinates(45., 39.))
                .build();
        UserDTO user3 = UserDTO.builder()
                .id("3")
                .coordinates(new Coordinates(12., 45.))
                .build();
        List<UserDTO> users = List.of(user1, user2, user3);
        when(userService.findAllUsers()).thenReturn(users);
        when(analysisService.getUsersMatching(1, 2))
                .thenReturn(Map.of("overall", 40.));
        when(analysisService.getUsersMatching(1, 3))
                .thenReturn(Map.of("overall", 34.));
        when(mapAPIService.getReverseGeocoding(any()))
                .thenReturn(user2.withLocation(new Location())
                        .withDistance(5.))
                .thenReturn(user3.withLocation(new Location())
                        .withDistance(6.));
        when(userService.findUserById(1)).thenReturn(user1);
        Collection<UserDTO> closestMatchingUsers = locationService.findBestMatchingUsers(1);
        assertEquals(3, closestMatchingUsers.size());
        verify(userService, times(1)).findAllUsers();
        verify(analysisService, times(2)).getUsersMatching(anyLong(), anyLong());
        verify(mapAPIService, times(2)).getReverseGeocoding(any());
    }

    @Test
    void shouldFindBestMatchingUsersInvalidId() {
        when(userService.findUserById(1)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> locationService.findBestMatchingUsers(1));
    }

    @Test
    void shouldFindBestMatchingUsersNoCoordinates() {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(50., 54.))
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .build();
        UserDTO user3 = UserDTO.builder()
                .id("3")
                .build();
        List<UserDTO> users = List.of(user1, user2, user3);
        when(userService.findAllUsers()).thenReturn(users);
        when(userService.findUserById(1)).thenReturn(user1);
        Collection<UserDTO> matchingUsers = locationService.findBestMatchingUsers(1);
        assertEquals(1, matchingUsers.size());
        verify(userService, times(1)).findAllUsers();
        verifyNoInteractions(mapAPIService, analysisService);
    }

    @Test
    void shouldFindUsersNearbyValidId() {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(50.5, 54.3))
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .coordinates(new Coordinates(50.3, 54.54))
                .build();
        UserDTO user3 = UserDTO.builder()
                .id("3")
                .coordinates(new Coordinates(12.3, 45.0))
                .build();
        List<UserDTO> users = List.of(user1, user2, user3);
        when(userService.findUserById(1L)).thenReturn(user1);
        when(userService.findAllUsers()).thenReturn(users);
        when(mapAPIService.getReverseGeocoding(argThat(user1::isNearby)))
                .thenReturn(user2);
        when(mapAPIService.getDistanceMatrix(argThat(arg ->
                arg.size() == 2 && arg.get(0)
                        .getId()
                        .equals(user1.getId()) && arg.get(1).isNearby(user1)
        ))).thenReturn(users);
        Collection<UserDTO> usersNearby = locationService.findUsersNearby(1L);
        assertEquals(3, usersNearby.size());
        verify(userService, times(1)).findUserById(anyLong());
        verify(userService, times(1)).findAllUsers();
        verify(mapAPIService, times(1)).getReverseGeocoding(any());
        verify(mapAPIService, times(1)).getDistanceMatrix(anyList());
    }

    @Test
    void shouldFindUsersNearbyInvalidId() {
        when(userService.findUserById(1)).thenThrow(UserNotFoundException.class);
        assertThrows(UserNotFoundException.class, () -> locationService.findUsersNearby(1));
    }

    @Test
    void shouldFindUsersNearbyNoCoordinatesNoNearby() {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(50.5, 54.3))
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .build();
        UserDTO user3 = UserDTO.builder()
                .id("3")
                .coordinates(new Coordinates(12.3, 45.0))
                .build();
        List<UserDTO> users = List.of(user1, user2, user3);
        when(userService.findUserById(1L)).thenReturn(user1);
        when(userService.findAllUsers()).thenReturn(users);

        Collection<UserDTO> usersNearby = locationService.findUsersNearby(1L);
        assertEquals(1, usersNearby.size());
        verify(userService, times(1)).findUserById(anyLong());
        verify(userService, times(1)).findAllUsers();
        verifyNoInteractions(mapAPIService);
    }

    @Test
    void shouldCalculateDistanceHaversine() {
        UserDTO baseUser = UserDTO.builder()
                .id("1")
                .coordinates(new Coordinates(17.12664026741288,51.14271806954306))
                .build();

        UserDTO user = UserDTO.builder()
                .id("2")
                .coordinates(new Coordinates(17.076744434118698, 51.12063051665086))
                .build();

        UserDTO returnUser = locationService.calculateDistanceHaversine(baseUser, user);
        assertEquals(4.260747329405308, returnUser.getDistance());

    }

    @Test
    void shouldGetMapAccessToken() {
        String mapAccessToken = locationService.getMapAccessToken();
        assertNotNull(mapAccessToken);
    }

}