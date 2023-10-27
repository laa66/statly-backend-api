package com.laa66.statlyapp.oauth2;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.exception.UserAuthenticationException;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.service.BetaUserService;
import com.laa66.statlyapp.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceUnitTest {

    @Mock
    BetaUserService betaUserService;

    @Mock
    UserService userService;

    @InjectMocks
    CustomOAuth2UserService customOAuth2UserService;

    private final UserDTO userDTO = UserDTO.builder()
            .id("1")
            .images(List.of())
            .email("test@mail.com")
            .points(0)
            .build();

    private final OAuth2UserWrapper oAuth2UserWrapper = new OAuth2UserWrapper(
            new DefaultOAuth2User(
                    Collections.singletonList(new OAuth2UserAuthority(Map.of("user","user"))),
                    Map.of(
                            "display_name", "user", "email", "test@mail.com",
                            "images", List.of(), "id", "id"),
                    "display_name"
            )
    );

    @Test
    void shouldGetUserActivated() {
        BetaUserDTO betaUserDTO = new BetaUserDTO("name", "test@mail.com", "date", true);
        when(userService.findUserByEmail("test@mail.com")).thenReturn(userDTO);
        when(betaUserService.findBetaUserByEmail("test@mail.com")).thenReturn(betaUserDTO);

        OAuth2UserWrapper result = (OAuth2UserWrapper) customOAuth2UserService.getUserOrCreate(oAuth2UserWrapper);

        assertNotNull(result);
        assertEquals(oAuth2UserWrapper.getAuthorities(), result.getAuthorities());
        assertEquals(oAuth2UserWrapper.getEmail(), result.getEmail());
        assertEquals(1L, result.getUserId());
        assertEquals(oAuth2UserWrapper.getDisplayName(), result.getDisplayName());
    }

    @Test
    void shouldGetUserNotActivated() {
        BetaUserDTO betaUserDTO = new BetaUserDTO("name", "test@mail.com", "date", false);
        when(userService.findUserByEmail("test@mail.com")).thenReturn(userDTO);
        when(betaUserService.findBetaUserByEmail("test@mail.com")).thenReturn(betaUserDTO);
        assertThrows(UserAuthenticationException.class, () -> customOAuth2UserService.getUserOrCreate(oAuth2UserWrapper));
    }

    @Test
    void shouldCreateUserActivated() {
        BetaUserDTO betaUserDTO = new BetaUserDTO("name", "test@mail.com", "date", true);
        OAuth2UserWrapper oAuth2User = new OAuth2UserWrapper(
                new DefaultOAuth2User(
                        Collections.singletonList(new OAuth2UserAuthority(Map.of("user","user"))),
                        Map.of(
                                "display_name", "user", "email", "test@mail.com",
                                "images", List.of(
                                        Map.of("url", "imageUrl", "height", 200, "width", 200),
                                        Map.of("url", "imageUrlSmaller", "height", 150, "width", 150)
                                ), "id", "id"),
                        "display_name"
                )
        );
        when(userService.findUserByEmail("test@mail.com")).thenReturn(null);
        when(userService.saveUser(argThat(arg -> arg
                .getImage()
                .equalsIgnoreCase("imageUrl"))))
                .thenReturn(userDTO);
        when(betaUserService.findBetaUserByEmail("test@mail.com")).thenReturn(betaUserDTO);

        OAuth2UserWrapper result = (OAuth2UserWrapper) customOAuth2UserService.getUserOrCreate(oAuth2User);
        assertNotNull(result);
        assertEquals(oAuth2User.getAuthorities(), result.getAuthorities());
        assertEquals(oAuth2User.getEmail(), result.getEmail());
        assertEquals(1L, result.getUserId());
        assertEquals(oAuth2User.getDisplayName(), result.getDisplayName());
    }

    @Test
    void shouldCreateUserNotActivated() {
        BetaUserDTO betaUserDTO = new BetaUserDTO("name", "test@mail.com", "date", false);
        OAuth2UserWrapper oAuth2User = new OAuth2UserWrapper(
                new DefaultOAuth2User(
                        Collections.singletonList(new OAuth2UserAuthority(Map.of("user","user"))),
                        Map.of(
                                "display_name", "user", "email", "test@mail.com",
                                "images", List.of(
                                        Map.of("url", "imageUrl", "height", 200, "width", 200),
                                        Map.of("url", "imageUrlSmaller", "height", 150, "width", 150)
                                ), "id", "id"),
                        "display_name"
                )
        );
        when(userService.findUserByEmail("test@mail.com")).thenReturn(null);
        when(userService.saveUser(argThat(arg -> arg
                .getImage()
                .equalsIgnoreCase("imageUrl"))))
                .thenReturn(userDTO);
        when(betaUserService.findBetaUserByEmail("test@mail.com")).thenReturn(betaUserDTO);
        assertThrows(UserAuthenticationException.class, () -> customOAuth2UserService.getUserOrCreate(oAuth2User));
    }

}