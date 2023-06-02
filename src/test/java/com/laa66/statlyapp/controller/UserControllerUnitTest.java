package com.laa66.statlyapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.MailService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(SpringExtension.class)
@Import(TestSecurityConfig.class)
@MockBeans({@MockBean(TrackRepository.class),
        @MockBean(ArtistRepository.class), @MockBean(GenreRepository.class),
        @MockBean(UserRepository.class), @MockBean(BetaUserRepository.class)})
class UserControllerUnitTest {

    @MockBean
    @Qualifier("restTemplate")
    RestTemplate restTemplate;

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @MockBean
    UserService userService;

    @MockBean
    MailService mailService;

    @Autowired
    UserController userController;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldAuthUser() throws Exception {
        Image image = new Image();
        image.setUrl("imageurl");
        UserDTO userDTO = new UserDTO("testuser", "test@mail.com", "testuser", List.of(image));
        when(spotifyAPIService.getCurrentUser()).thenReturn(userDTO);
        when(userService.authenticateUser(userDTO)).thenReturn("url/callback?name=testuser&url=imageurl");
        mockMvc.perform(MockMvcRequestBuilders.get("/user/auth").with(oauth2Login()))
                .andExpect(status().isTemporaryRedirect())
                .andExpect(header().string("location", "url/callback?name=testuser&url=imageurl"));
    }

    @Test
    void shouldNotAuth() throws Exception {
        mockMvc.perform(get("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldDelete() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/user/delete").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldFindAllBetaUsers() throws Exception {
        List<BetaUserDTO> betaUsers = List.of(
                new BetaUserDTO("user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0).toString()),
                new BetaUserDTO("user2", "user2@email.com", LocalDateTime.of(2023, 1,1,11, 0, 0).toString()));
        when(userService.findAllBetaUsers()).thenReturn(betaUsers);
        mockMvc.perform(get("/user/beta/all")
                        .with(oauth2Login().attributes(map ->
                                map.put("email", "admin@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is("user1")))
                .andExpect(jsonPath("$[0].email", is("user1@mail.com")))
                .andExpect(jsonPath("$[1].fullName", is("user2")))
                .andExpect(jsonPath("$[1].email", is("user2@email.com")))
                .andDo(print());
    }

    @Test
    void shouldFindAllBetaUsersWrongEmail() throws Exception {
        mockMvc.perform(get("/user/beta/all").with(oauth2Login().attributes(map ->
                                map.put("email", "wrong@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldJoinBeta() throws Exception {
        BetaUserDTO dto = new BetaUserDTO("name", "email", null);
        mockMvc.perform(post("/user/beta/join").with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        verify(userService, times(1)).saveBetaUser(isA(BetaUserDTO.class));
        verify(mailService, times(1)).sendJoinBetaNotification();
    }

    @Test
    void shouldSentNotification() throws Exception {
        BetaUserDTO dto = new BetaUserDTO("name", "email", null);
        mockMvc.perform(post("/user/beta/notification").with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
        verify(mailService, times(1)).sendAccessGrantedNotification(any());
    }

    @Test
    void shouldDeleteAllBetaUsers() throws Exception {
        mockMvc.perform(delete("/user/beta/delete").with(oauth2Login().attributes(map ->
                                map.put("email", "admin@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldDeleteAllBetaUsersWrongEmail() throws Exception {
        mockMvc.perform(delete("/user/beta/delete").with(oauth2Login().attributes(map ->
                                map.put("email", "wrong@mail.com")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


}
