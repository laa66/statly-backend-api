package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.ProfileDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.spotify.Image;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.UserService;
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
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocialController.class)
@Import(TestSecurityConfig.class)
class SocialControllerIntegrationTest {

    @MockBean
    SpotifyTokenRepository spotifyTokenRepository;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    UserService userService;

    @MockBean
    SocialService socialService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    OAuth2AuthenticationToken token;

    @BeforeEach
    void setup() {
        token = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of("display_name", "name", "userId", 1L, "country", "ES"), "display_name"
        )), Collections.emptyList(), "client");
        when(jwtProvider.validateToken("token")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token")).thenReturn(1L);
        when(spotifyTokenRepository.getToken(1L)).thenReturn(token);
    }

    @Test
    void shouldGetUserFollowing() throws Exception {
        FollowersDTO followersDTO = new FollowersDTO(1, List.of(
                UserDTO.builder()
                        .id("id")
                        .name("name")
                        .images(List.of(new Image("url", null, null)))
                        .build()));
        when(socialService.getFollowers(1, StatlyConstants.FOLLOWING))
                .thenReturn(followersDTO);
        mockMvc.perform(get("/member/me/following")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size", is(1)),
                        jsonPath("$.users[0].id", is("id")),
                        jsonPath("$.users[0].display_name", is("name")),
                        jsonPath("$.users[0].images[0].url", is("url"))
                );

        mockMvc.perform(get("/member/me/following")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldFollowValidFollowId() throws Exception {
        mockMvc.perform(put("/member/follow?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(socialService, times(1)).follow(1, 2);

        mockMvc.perform(put("/member/follow?user_id=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldFollowNotValidFollowId() throws Exception {
        when(socialService.follow(1, 3)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(put("/member/follow?user_id=3")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUnfollowValidFollowId() throws Exception {
        mockMvc.perform(put("/member/unfollow?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(socialService, times(1)).unfollow(1, 2);

        mockMvc.perform(put("/member/unfollow?user_id=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldUnfollowNotValidFollowId() throws Exception {
        when(socialService.unfollow(1, 3)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(put("/member/unfollow?user_id=3")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetProfileValidUserId() throws Exception {
        ProfileDTO profileDTO = new ProfileDTO(
                1,
                "externalId",
                "username",
                "url",
                null,
                null,
                null,
                null,
                null,
                Map.of(),
                null,
                null,
                null,
                500
        );
        when(socialService.getUserProfile(1)).thenReturn(profileDTO);
        mockMvc.perform(get("/member/profile?user_id=1")
                        .header("Authorization", "Bearer token"))
                .andExpectAll(status().isOk(),
                        jsonPath("$.username", is("username")),
                        jsonPath("$.imageUrl", is("url")),
                        jsonPath("$.points", is(500)));

        mockMvc.perform(get("/user/profile?user_id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetProfileNotValidId() throws Exception {
        when(socialService.getUserProfile(2)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(get("/member/profile?user_id=2")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldGetRank() throws Exception {
        when(userService.findAllUsersOrderByPoints()).thenReturn(List.of(
                UserDTO.builder()
                        .id("id2")
                        .points(300)
                        .build(),
                UserDTO.builder()
                        .id("id1")
                        .points(150)
                        .build()));
        mockMvc.perform(get("/member/rank")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isArray(),
                        jsonPath("$[1]").hasJsonPath()
                );
        mockMvc.perform(get("/member/profile?user_id=1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldAddLinks() throws Exception {
        Map<String, String> links = new LinkedHashMap<>();
        links.put("fb", "fb");
        links.put("ig", "ig");
        links.put("twitter", null);
        mockMvc.perform(put("/member/links")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(links)))
                .andExpect(status().isNoContent());

        verify(socialService, times(1)).updateSocialLinks(1, links);

        mockMvc.perform(put("/member/links")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldSaveLocation() throws Exception {
        Coordinates coordinates = new Coordinates(53.4312, -22.3345);
        mockMvc.perform(post("/member/location")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer token")
                .content(mapper.writeValueAsString(coordinates)))
                .andExpect(status().isNoContent());
        verify(socialService, times(1)).saveUserLocation(1, 53.4312, -22.3345);

        mockMvc.perform(post("/member/location")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(coordinates)))
                .andExpect(status().isFound());
    }

}