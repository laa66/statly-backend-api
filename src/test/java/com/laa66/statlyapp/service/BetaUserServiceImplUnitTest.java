package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.repository.BetaUserRepository;
import com.laa66.statlyapp.service.impl.BetaUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

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
    void shouldSaveBetaUser() {
        betaUserService.saveBetaUser(new BetaUserDTO("name", "email", "date"));
        verify(betaUserRepository, times(1)).save(any());
    }

    @Test
    void shouldFindAllBetaUsers() {
        List<BetaUser> betaUsers = List.of(
                new BetaUser(1, "user1", "user1@mail.com", LocalDateTime.of(2023, 1, 1, 12, 0, 0)),
                new BetaUser(2, "user2", "user2@gmail.com", LocalDateTime.of(2023, 1,1,11, 0, 0)));
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

}