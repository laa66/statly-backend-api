package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.impl.SocialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SocialServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SocialServiceImpl socialService;

    @Test
    void shouldFollowValidIds() {
        User fromUser = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User toUser = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
        when(userRepository.save(fromUser)).thenReturn(fromUser);
        User returnUser = socialService.follow(1L, 2L);
        assertEquals(fromUser.getId(), returnUser.getId());
        assertEquals(1, fromUser.getFollowing().size());
    }

    @Test
    void shouldFollowNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1L, 2L));
    }

    @Test
    void shouldFollowNotValidFollowId() {
        User fromUser = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1L, 2L));
    }

    @Test
    void shouldGetFollowingValidUserId() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User followedUser = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        user.addFollower(followedUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1L, StatlyConstants.FOLLOWING);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());
    }

    @Test
    void shouldGetFollowingNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(1L, StatlyConstants.FOLLOWING));
    }

    @Test
    void shouldGetFollowersValidUserId() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User userFollower = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        user.setFollowers(List.of(userFollower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1L, StatlyConstants.FOLLOWERS);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());
    }

    @Test
    void shouldGetFollowersNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(1L, StatlyConstants.FOLLOWERS));
    }


}
