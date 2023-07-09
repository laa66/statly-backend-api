package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.config.TestSecurityConfig;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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

    @MockBean
    SocialService socialService;

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

        mockMvc.perform(get("/user/auth")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldDelete() throws Exception {
        doNothing().when(userService).deleteUser(1L);
        mockMvc.perform(delete("/user/me/delete").with(oauth2Login()
                                .attributes(map -> map.put("userId", 1L)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/user/me/delete")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldSearchUser() throws Exception {
        User user = new User("1", "uri", "username", List.of());
        when(userService.findAllMatchingUsers("name")).thenReturn(List.of(user));
        mockMvc.perform(get("/user/search?username=name")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$[0].id", is("1")),
                        jsonPath("$[0].display_name", is("username"))
                );

        when(userService.findAllMatchingUsers("none")).thenReturn(List.of());
        mockMvc.perform(get("/user/search?username=none")
                .with(oauth2Login())
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
        User user = new User("1", "uri", "name", List.of());
        when(userService.findUserById(1L)).thenReturn(user);
        mockMvc.perform(get("/user/me")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is("1")),
                        jsonPath("$.display_name", is("name"))
                );

        when(userService.findUserById(2L)).thenThrow(UserNotFoundException.class);
        mockMvc.perform(get("/user/me")
                .with(oauth2Login().attributes(map -> map.put("userId", 2L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetUserFollowing() throws Exception {
        FollowersDTO followersDTO = new FollowersDTO(1, List.of(new User(
                "id", "uri", "name", List.of(new Image("url", null, null))
        )));
        when(socialService.getFollowers(1, StatlyConstants.FOLLOWING))
                .thenReturn(followersDTO);
        mockMvc.perform(get("/user/me/following")
                .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.size", is(1)),
                        jsonPath("$.users[0].id", is("id")),
                        jsonPath("$.users[0].display_name", is("name")),
                        jsonPath("$.users[0].images[0].url", is("url"))
                );

        mockMvc.perform(get("/user/me/following")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldFollowValidFollowId() throws Exception {
        mockMvc.perform(put("/user/follow?user_id=2")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(socialService, times(1)).follow(1, 2);

        mockMvc.perform(put("/user/follow?user_id=2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldFollowNotValidFollowId() throws Exception {
        when(socialService.follow(1, 3)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(put("/user/follow?user_id=3")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUnfollowValidFollowId() throws Exception {
        mockMvc.perform(put("/user/unfollow?user_id=2")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(socialService, times(1)).unfollow(1, 2);

        mockMvc.perform(put("/user/unfollow?user_id=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isFound());
    }

    @Test
    void shouldUnfollowNotValidFollowId() throws Exception {
        when(socialService.unfollow(1, 3)).thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(put("/user/unfollow?user_id=3")
                        .with(oauth2Login().attributes(map -> map.put("userId", 1L)))
                        .with(csrf())
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
                500
        );
        when(socialService.getUserProfile(1)).thenReturn(profileDTO);
        mockMvc.perform(get("/user/profile?user_id=1")
                .with(oauth2Login())
                .contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(get("/user/profile?user_id=2")
                        .with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
