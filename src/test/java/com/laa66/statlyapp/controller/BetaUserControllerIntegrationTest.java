package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.jwt.JwtProvider;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.repository.SpotifyTokenRepository;
import com.laa66.statlyapp.service.BetaUserService;
import com.laa66.statlyapp.service.MailService;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetaUserController.class)
@Import(TestSecurityConfig.class)
class BetaUserControllerIntegrationTest {

    @MockBean
    SpotifyTokenRepository spotifyTokenRepository;

    @MockBean
    JwtProvider jwtProvider;

    @MockBean
    BetaUserService betaUserService;

    @MockBean
    MailService mailService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    OAuth2AuthenticationToken token1;
    OAuth2AuthenticationToken token2;

    @BeforeEach
    void setup() {
        token1 = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of("display_name", "name", "userId", 1L, "country", "ES", "email", "admin@mail.com"), "display_name"
        )), Collections.emptyList(), "client");
        token2 = new OAuth2AuthenticationToken(new OAuth2UserWrapper(new DefaultOAuth2User(
                Collections.emptyList(), Map.of("display_name", "name", "userId", 2L, "country", "ES", "email", "user@mail.com"), "display_name"
        )), Collections.emptyList(), "client");
        when(jwtProvider.validateToken("token")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token")).thenReturn(1L);
        when(spotifyTokenRepository.getToken(1L)).thenReturn(token1);
    }

    @Test
    void shouldGetAllBetaUsers() throws Exception {
        List<BetaUserDTO> betaUsers = List.of(
                new BetaUserDTO("user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0).toString()),
                new BetaUserDTO("user2", "user2@email.com", LocalDateTime.of(2023, 1,1,11, 0, 0).toString()));
        when(betaUserService.findAllBetaUsers()).thenReturn(betaUsers);
        mockMvc.perform(get("/beta/all")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is("user1")))
                .andExpect(jsonPath("$[0].email", is("user1@mail.com")))
                .andExpect(jsonPath("$[1].fullName", is("user2")))
                .andExpect(jsonPath("$[1].email", is("user2@email.com")));
    }

    @Test
    void shouldGetAllBetaUsersWrongEmail() throws Exception {
        when(jwtProvider.validateToken("token2")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token2")).thenReturn(2L);
        when(spotifyTokenRepository.getToken(2L)).thenReturn(token2);
        mockMvc.perform(get("/beta/all")
                        .header("Authorization", "Bearer token2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldJoinBeta() throws Exception {
        mockMvc.perform(get("/beta/join")
                        .header("Authorization", "Bearer token")
                        .param("name", "name")
                        .param("email", "email"))
                .andExpect(status().isNoContent());
        verify(betaUserService, times(1)).saveBetaUser(isA(BetaUserDTO.class));
        verify(mailService, times(1)).sendJoinBetaNotification();
    }

    @Test
    void shouldSentNotification() throws Exception {
        BetaUserDTO dto = new BetaUserDTO("name", "email", null);
        mockMvc.perform(post("/beta/notification")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        verify(mailService, times(1)).sendAccessGrantedNotification(any());
    }

    @Test
    void shouldDeleteAllBetaUsers() throws Exception {
        mockMvc.perform(delete("/beta/delete")
                        .header("Authorization", "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteAllBetaUsersWrongEmail() throws Exception {
        when(jwtProvider.validateToken("token2")).thenReturn(true);
        when(jwtProvider.getIdFromToken("token2")).thenReturn(2L);
        when(spotifyTokenRepository.getToken(2L)).thenReturn(token2);
        mockMvc.perform(delete("/beta/delete")
                        .header("Authorization", "Bearer token2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
