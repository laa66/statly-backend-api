package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserStats;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.Track;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SocialServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    StatsService statsService;

    @InjectMocks
    SocialServiceImpl socialService;

    @Test
    void shouldGetUserProfileValidUserId() {
        User user = new User(
                1L,
                "id",
                "username",
                "test@mail.com",
                "url",
                LocalDateTime.of(2022, 11, 20, 20, 20),
                new UserStats(1, 40.0, 30.0, 50.0, 300.0, 500));
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(List.of(new Artist("artist")), "title"))
                , "2", "long", null);
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist("artist"))
                , "long", null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(statsService.getUserTracks(1, "long")).thenReturn(tracksDTO);
        when(statsService.getUserArtists(1, "long")).thenReturn(artistsDTO);
        ProfileDTO profileDTO = socialService.getUserProfile(1);
        assertEquals(1L, profileDTO.getId());
        assertEquals("username", profileDTO.getUsername());
        assertEquals("url", profileDTO.getImageUrl());
        assertEquals(500, profileDTO.getPoints());
        assertEquals(user.getJoinDate(), profileDTO.getJoinDate());
        assertEquals("title", profileDTO.getTopTracks().get(0).getName());
        assertEquals("artist", profileDTO.getTopArtists().get(0).getName());
        assertEquals(50.0, profileDTO.getStatsMap().get("mainstream"));

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getUserProfile(2L));
    }

    @Test
    void shouldFollowValidIds() {
        User user = new User(1L,"id","username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        User userFollowed = new User(2L,"id","username2","test2@mail.com", "url2", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userFollowed));
        when(userRepository.save(user)).thenReturn(user);
        User returnUser = socialService.follow(1, 2);
        assertEquals(user.getId(), returnUser.getId());
        assertEquals(1, returnUser.getFollowing().size());
    }

    @Test
    void shouldFollowNotValidIds() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(3, 2));

        User fromUser = new User(1L, "id","username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1, 2));
    }

    @Test
    void shouldGetFollowingValidUserId() {
        User user = new User(1L, "id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        User followedUser = new User(2L, "id","username2","test2@mail.com", "url2", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        user.addFollower(followedUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1, StatlyConstants.FOLLOWING);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());

        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(1, StatlyConstants.FOLLOWING));
    }

    @Test
    void shouldGetFollowersValidUserId() {
        User user = new User(1L,"id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        User userFollower = new User(2L,"id", "username2","test2@mail.com", "url2", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        user.setFollowers(List.of(userFollower));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        FollowersDTO followersDTO = socialService.getFollowers(1, StatlyConstants.FOLLOWERS);
        assertEquals(1, followersDTO.getSize());
        assertEquals("2", followersDTO.getUsers().get(0).getId());
        assertEquals("username2", followersDTO.getUsers().get(0).getName());
        assertEquals("url2", followersDTO.getUsers().get(0).getImages().get(0).getUrl());

        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getFollowers(3, StatlyConstants.FOLLOWERS));
    }

    @Test
    void shouldUnfollowValidUserId() {
        User user = new User(1L, "id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        User followedUser = new User(2L,"id", "username2","test2@mail.com", "url2", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
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
    void shouldUnfollowNotValidIds() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.unfollow(3, 2));

        User fromUser = new User(1L, "id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats());
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.unfollow(1, 2));

    }

    @Test
    void shouldUpdatePointsValidUserId() {
        User user = new User(1L, "id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats(
                1, 0,0,0,0,0
        ));
        User userToSave = new User(1L, "id", "username1","test1@mail.com", "url1", LocalDateTime.of(2022, 11, 20, 20, 20), new UserStats(
                1, 0, 0, 0, 0, 10
        ));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userToSave);
        User savedUser = socialService.updatePoints(1L, 10);
        assertEquals(10, savedUser.getUserStats().getPoints());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.updatePoints(2L, 10));
    }

}
