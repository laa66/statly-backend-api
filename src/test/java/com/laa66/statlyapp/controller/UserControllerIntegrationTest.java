package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @MockBean
    SpotifyTokenRepository spotifyTokenRepository;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

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
    void shouldDelete() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/user/me/delete")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/user/me/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldSearchUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id("1")
                .name("username")
                .build();
        when(userService.findAllMatchingUsers("name")).thenReturn(List.of(userDTO));
        mockMvc.perform(get("/user/search?username=name")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", is("1")),
                        jsonPath("$[0].display_name", is("username"))
                );

        when(userService.findAllMatchingUsers("none")).thenReturn(List.of());
        mockMvc.perform(get("/user/search?username=none")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$").isEmpty()
                );

        mockMvc.perform(get("/user/search?username=name")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldGetCurrentUser() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id("1")
                .name("name")
                .build();
        when(userService.findUserById(1L)).thenReturn(userDTO);
        mockMvc.perform(get("/user/me")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is("1")),
                        jsonPath("$.display_name", is("name"))
                );

        when(userService.findUserById(2L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/user/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }
}
