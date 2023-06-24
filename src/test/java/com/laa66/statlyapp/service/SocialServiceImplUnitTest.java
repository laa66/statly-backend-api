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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SocialServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    SocialServiceImpl socialService;

    @Test
    void shouldFollowValidIds() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User userFollowed = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userFollowed));
        when(userRepository.save(user)).thenReturn(user);
        User returnUser = socialService.follow(1, 2);
        assertEquals(user.getId(), returnUser.getId());
        assertEquals(1, returnUser.getFollowing().size());
    }

    @Test
    void shouldFollowNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1, 2));
    }

    @Test
    void shouldFollowNotValidFollowId() {
        User fromUser = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1, 2));
    }

    @Test
    void shouldGetFollowingValidUserId() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User followedUser = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        user.addFollower(followedUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1, StatlyConstants.FOLLOWING);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());
    }

    @Test
    void shouldGetFollowingNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(1, StatlyConstants.FOLLOWING));
    }

    @Test
    void shouldGetFollowersValidUserId() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User userFollower = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        user.setFollowers(List.of(userFollower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1, StatlyConstants.FOLLOWERS);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());
    }

    @Test
    void shouldGetFollowersNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(1, StatlyConstants.FOLLOWERS));
    }

    @Test
    void shouldUnfollowValidUserId() {
        User user = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        User followedUser = new User(2L, "username2","test2@mail.com", "url2", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        user.addFollower(followedUser);
        followedUser.setFollowers(List.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(followedUser));
        when(userRepository.save(user)).thenReturn(user);
        User returnUser = socialService.unfollow(1, 2);
        assertEquals(1L, returnUser.getId());
        assertEquals(0, returnUser.getFollowing().size());
    }

    @Test
    void shouldUnfollowNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.unfollow(1, 2));
    }

    @Test
    void shouldUnfollowNotValidUnfollowId() {
        User fromUser = new User(1L, "username1","test1@mail.com", "url1", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.unfollow(1, 2));
    }


}
