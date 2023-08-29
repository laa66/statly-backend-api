package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void shouldFindAllUsers() {
        User user1 = new User().withId(1)
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());
        User user2 = new User().withId(2)
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());
        when(userRepository.findAll())
                .thenReturn(List.of(user1, user2))
                .thenReturn(List.of());
        List<UserDTO> users = (List<UserDTO>) userService.findAllUsers();
        assertEquals(2, users.size());
        assertEquals("1", users.get(0).getId());
        assertEquals("2", users.get(1).getId());

        Collection<UserDTO> emptyUsers = userService.findAllUsers();
        assertTrue(emptyUsers.isEmpty());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User()
                .withId(1L)
                .withUsername("username")
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo())
                .withImage("url");
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        UserDTO returnUser = userService.findUserByEmail("user@mail.com");
        assertEquals("1", returnUser.getId());
        assertEquals(user.getUsername(), returnUser.getName());
        assertEquals(user.getImage(), returnUser.getImages().get(0).getUrl());

        when(userRepository.findByEmail("wrong@mail.com")).thenReturn(Optional.empty());
        UserDTO returnEmptyUser = userService.findUserByEmail("wrong@mail.com");
        assertNull(returnEmptyUser);
    }

    @Test
    void shouldFindAllMatchingUsers() {
        User user = new User()
                .withId(1L)
                .withUsername("username")
                .withUserStats(new UserStats())
                .withImage("url")
                .withUserInfo(new UserInfo());
        when(userRepository.findAllMatchingUsers("name")).thenReturn(List.of(user));
        List<UserDTO> users = userService.findAllMatchingUsers("name");
        assertEquals(1, users.size());
        assertEquals("username", users.get(0).getName());
        assertEquals("url", users.get(0).getImages().get(0).getUrl());

        when(userRepository.findAllMatchingUsers("none")).thenReturn(List.of());
        List<UserDTO> emptyUsers = userService.findAllMatchingUsers("none");
        assertTrue(emptyUsers.isEmpty());
    }

    @Test
    void shouldFindAllUsersOrderByPoints() {
        User user1 = new User()
                .withId(1L)
                .withUserStats(new UserStats(1, 0., 0., 0., 0., 130, 0))
                .withUserInfo(new UserInfo());
        User user2 = new User()
                .withId(2L)
                .withUserStats(new UserStats(2, 0., 0., 0., 0., 380, 0))
                .withUserInfo(new UserInfo());
        when(userRepository.findAllUsersOrderByPoints()).thenReturn(List.of(user2, user1));
        List<UserDTO> users = userService.findAllUsersOrderByPoints();
        assertEquals(2, users.size());
        assertEquals("2", users.get(0).getId());
        assertEquals("1", users.get(1).getId());
    }

    @Test
    void shouldSaveUser() {
        User beforeSaveUser = new User()
                .withId(0L)
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());
        User afterSaveUser = new User()
                .withId(1L)
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());
        when(userRepository.save(beforeSaveUser)).thenReturn(afterSaveUser);
        UserDTO returnUser = userService.saveUser(beforeSaveUser);
        verify(userRepository, times(1)).save(beforeSaveUser);
        assertNotEquals("0", returnUser.getId());
        assertEquals("1", returnUser.getId());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User()
                .withId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(1L);

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
    }
}