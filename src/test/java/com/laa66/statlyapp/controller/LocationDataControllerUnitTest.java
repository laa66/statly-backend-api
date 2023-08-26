package com.laa66.statlyapp.controller;

import com.icegreen.greenmail.configuration.UserBean;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import com.laa66.statlyapp.service.LocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LocationDataController.class)
@Import(TestSecurityConfig.class)
class LocationDataControllerUnitTest {

    @MockBean
    SpotifyTokenRepository spotifyTokenRepository;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    LocationService locationService;

    @Autowired
    MockMvc mockMvc;

    OAuth2AuthenticationToken token1;

    @BeforeEach
    void setup() {
        token1 = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of("display_name", "name", "userId", 1L, "country", "ES", "email", "admin@mail.com"), "display_name"
        )), Collections.emptyList(), "client");
        when(jwtProvider.validateToken("token")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token")).thenReturn(1L);
        when(spotifyTokenRepository.getToken(1L)).thenReturn(token1);
    }

    @Test
    void shouldFindClosestMatchingUsersValid() throws Exception {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .build();
        when(locationService.findBestMatchingUsers(1L))
                .thenReturn(List.of(user1, user2));
        mockMvc.perform(get("/api/location/users/matching")
                .header("Authorization", "Bearer token")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2))
                );

        mockMvc.perform(get("/api/location/users/matching")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldFindUsersNearby() throws Exception {
        UserDTO user1 = UserDTO.builder()
                .id("1")
                .build();
        UserDTO user2 = UserDTO.builder()
                .id("2")
                .build();
        when(locationService.findUsersNearby(1L))
                .thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/location/users/nearby")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2))
                );

        mockMvc.perform(get("/api/location/users/nearby")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}