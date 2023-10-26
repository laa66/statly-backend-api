package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.repository.BetaUserRepository;
import com.laa66.statlyapp.service.impl.BetaUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BetaUserServiceImplUnitTest {

    @Mock
    BetaUserRepository betaUserRepository;

    @InjectMocks
    BetaUserServiceImpl betaUserService;

    @Test
    void shouldFindBetaUserByEmailExists() {
        BetaUser betaUser = new BetaUser(1, "user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0), true);
        when(betaUserRepository.findByEmail("user1@mail.com")).thenReturn(Optional.of(betaUser));
        BetaUserDTO betaUserDTO = betaUserService.findBetaUserByEmail("user1@mail.com");
        assertEquals(betaUser.getFullName(), betaUserDTO.getFullName());
        assertEquals(betaUser.getEmail(), betaUserDTO.getEmail());
        verify(betaUserRepository, times(1)).findByEmail("user1@mail.com");
    }

    @Test
    void shouldFindBetaUserByEmailNotExists() {
        when(betaUserRepository.findByEmail("user1@mail.com")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> betaUserService.findBetaUserByEmail("user1@mail.com"));
    }

    @Test
    void shouldSaveBetaUser() {
        betaUserService.saveBetaUser(new BetaUserDTO("name", "email", "date", false));
        verify(betaUserRepository, times(1)).save(any());
    }

    @Test
    void shouldFindAllBetaUsers() {
        List<BetaUser> betaUsers = List.of(
                new BetaUser(1, "user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0), true),
                new BetaUser(2, "user2", "user2@gmail.com", LocalDateTime.of(2023, 1,1,11, 0, 0), true));
        when(betaUserRepository.findAll()).thenReturn(betaUsers);
        List<BetaUserDTO> dtoList = betaUserService.findAllBetaUsers();
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
        betaUserService.deleteAllBetaUsers();
        verify(betaUserRepository, times(1)).deleteAll();
    }

    @Test
    void shouldActivateUserExists() {
        BetaUser betaUser = new BetaUser(1, "name", "email", LocalDateTime.now(), true);
        when(betaUserRepository.findByEmail("email")).thenReturn(Optional.of(betaUser));
        when(betaUserRepository.save(betaUser)).thenReturn(betaUser);

        betaUserService.activateUser("email");
        verify(betaUserRepository, times(1)).findByEmail("email");
        verify(betaUserRepository, times(1)).save(betaUser);
    }

    @Test
    void shouldActivateUserNotExists() {
        when(betaUserRepository.findByEmail("wrongEmail")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> betaUserService.activateUser("wrongEmail"));
        verify(betaUserRepository, times(1)).findByEmail("wrongEmail");
        verify(betaUserRepository, never()).save(any());
    }

    @Test
    void shouldExistsByEmailExists() {
        when(betaUserRepository.existsByEmail("email")).thenReturn(true);
        when(betaUserRepository.existsByEmail("wrongEmail")).thenReturn(false);

        assertTrue(betaUserService.existsByEmail("email"));
        assertFalse(betaUserService.existsByEmail("wrongEmail"));

    }

}