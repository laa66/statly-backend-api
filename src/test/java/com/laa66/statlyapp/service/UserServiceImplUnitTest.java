package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.repository.*;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @Mock
    BetaUserRepository betaUserRepository;

    @Mock
    UserRepository userRepository;

    UserServiceImpl userService;

    @BeforeEach
    void setup() {
        userService =
                new UserServiceImpl(userRepository, betaUserRepository, "localhost");
    }

    @Test
    void shouldAuthenticateUserWithImage() {
        UserDTO userDTO = new UserDTO("id", "email", "name", List.of(new Image("url", 200, 200)));
        String redirectUrl = userService.authenticateUser(userDTO);
        assertEquals("localhost/statly-frontend/#/callback?name=" + userDTO.getDisplayName() + "&url=" + userDTO.getImages().get(0).getUrl(), redirectUrl);
    }

    @Test
    void shouldAuthenticateUserWithoutImage() {
        UserDTO userDTO = new UserDTO("id", "email", "name", List.of());
        String redirectUrl = userService.authenticateUser(userDTO);
        assertEquals("localhost/statly-frontend/#/callback?name=" + userDTO.getDisplayName() + "&url=" + "./account.png", redirectUrl);

    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User(1, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        Optional<User> returnUser = userRepository.findByEmail("user@mail.com");
        assertTrue(returnUser.isPresent());
        assertEquals(user.getId(), returnUser.get().getId());
        assertEquals(user.getEmail(), returnUser.get().getEmail());
        assertEquals(user.getJoinDate(), returnUser.get().getJoinDate());
    }

    @Test
    void shouldNotFindUserByEmail() {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.empty());
        Optional<User> returnUser = userRepository.findByEmail("user@mail.com");
        assertTrue(returnUser.isEmpty());
    }

    @Test
    void shouldSaveUser() {
        User beforeSaveUser = new User(0, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        User afterSaveUser = new User(1, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.save(beforeSaveUser)).thenReturn(afterSaveUser);
        User returnUser = userService.saveUser(beforeSaveUser);
        assertNotNull(returnUser);
        assertNotEquals(beforeSaveUser.getId(), returnUser.getId());
        assertEquals(afterSaveUser.getId(), returnUser.getId());
        assertEquals(beforeSaveUser.getEmail(), returnUser.getEmail());
        assertEquals(beforeSaveUser.getJoinDate(), returnUser.getJoinDate());
    }

    @Test
    void shouldDeleteUser() {
        User user = new User(1L, "user@mail.com", LocalDateTime.of(2023, 4, 30, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(user.getId());
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldNotDeleteUserThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
    }

    @Test
    void shouldSaveBetaUser() {
        userService.saveBetaUser(new BetaUserDTO("name", "email", "date"));
        verify(betaUserRepository, times(1)).save(any());
    }

    @Test
    void shouldFindAllBetaUsers() {
        List<BetaUser> betaUsers = List.of(
                new BetaUser(1, "user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0)),
                new BetaUser(2, "user2", "user2@gmail.com", LocalDateTime.of(2023, 1,1,11, 0, 0)));
        when(betaUserRepository.findAll()).thenReturn(betaUsers);
        List<BetaUserDTO> dtoList = userService.findAllBetaUsers();
        assertNotNull(dtoList);
        assertEquals(betaUsers.size(), dtoList.size());
        assertEquals(betaUsers.get(0).getFullName(), dtoList.get(0).getFullName());
        assertEquals(betaUsers.get(0).getEmail(), dtoList.get(0).getEmail());
        assertEquals(betaUsers.get(0).getDate().toString(), dtoList.get(0).getDate());
        assertEquals(betaUsers.get(1).getFullName(), dtoList.get(1).getFullName());
        assertEquals(betaUsers.get(1).getEmail(), dtoList.get(1).getEmail());
        assertEquals(betaUsers.get(1).getDate().toString(), dtoList.get(1).getDate());
    }

    @Test
    void shouldDeleteAllBetaUsers() {
        userService.deleteAllBetaUsers();
        verify(betaUserRepository, times(1)).deleteAll();
    }
}