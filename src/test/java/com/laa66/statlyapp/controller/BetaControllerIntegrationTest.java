package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.service.MailService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BetaController.class)
@Import(TestSecurityConfig.class)
public class BetaControllerIntegrationTest {

    @MockBean
    UserService userService;

    @MockBean
    MailService mailService;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldGetAllBetaUsers() throws Exception {
        List<BetaUserDTO> betaUsers = List.of(
                new BetaUserDTO("user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0).toString()),
                new BetaUserDTO("user2", "user2@email.com", LocalDateTime.of(2023, 1,1,11, 0, 0).toString()));
        when(userService.findAllBetaUsers()).thenReturn(betaUsers);
        mockMvc.perform(get("/beta/all")
                        .with(oauth2Login().attributes(map ->
                                map.put("email", "admin@mail.com")))
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
        mockMvc.perform(get("/beta/all").with(oauth2Login().attributes(map ->
                                map.put("email", "wrong@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldJoinBeta() throws Exception {
        mockMvc.perform(get("/beta/join").with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "name")
                        .param("email", "email"))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).saveBetaUser(isA(BetaUserDTO.class));
        verify(mailService, times(1)).sendJoinBetaNotification();
    }

    @Test
    void shouldSentNotification() throws Exception {
        BetaUserDTO dto = new BetaUserDTO("name", "email", null);
        mockMvc.perform(post("/beta/notification").with(oauth2Login()
                                .attributes(map ->
                                map.put("email", "admin@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        verify(mailService, times(1)).sendAccessGrantedNotification(any());
    }

    @Test
    void shouldDeleteAllBetaUsers() throws Exception {
        mockMvc.perform(delete("/beta/delete").with(oauth2Login().attributes(map ->
                                map.put("email", "admin@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteAllBetaUsersWrongEmail() throws Exception {
        mockMvc.perform(delete("/beta/delete").with(oauth2Login().attributes(map ->
                                map.put("email", "wrong@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
