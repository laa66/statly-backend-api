package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerIntegrationTest {

    @MockBean
    SpotifyAPIService spotifyAPIService;

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

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
    void shouldGetUser() throws Exception {
        User user = new User("1", "uri", "name", List.of(new Image("url", null, null)));
        when(userService.findUserByUsername("name")).thenReturn(user);
        mockMvc.perform(get("/user/get?username=name")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is("1")),
                        jsonPath("$.uri", is("uri")),
                        jsonPath("$.display_name", is("name")),
                        jsonPath("$.images[0].url", is("url"))
                );
    }

    @Test
    void shouldGetUserWrongUsername() throws Exception {
        when(userService.findUserByUsername("wrong"))
                .thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/user/get?username=wrong")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
