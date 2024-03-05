package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.entity.UserInfo;
import com.laa66.statlyapp.entity.UserStats;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.mapbox.Coordinates;
import com.laa66.statlyapp.model.spotify.Artist;
import com.laa66.statlyapp.model.spotify.Track;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.impl.SocialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class SocialServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    StatsService statsService;

    @InjectMocks
    SocialServiceImpl socialService;

    @Test
    void shouldGetUserProfile() {
        User user = new User()
                .withId(1L)
                .withExternalId("id")
                .withUsername("username")
                .withEmail("test@mail.com")
                .withImage("url")
                .withJoinDate(LocalDateTime.of(2022, 11, 20, 20, 20))
                .withUserStats(new UserStats(1, 40.0, 30.0, 50.0, 300.0, 500, 0))
                .withUserInfo(new UserInfo());
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(List.of(new Artist("artist")), "title"))
                , "2", "long", null, null);
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist("artist"))
                , "long", null, null);
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
        User user = new User()
                .withId(1L)
                .withUserStats(new UserStats());
        User userFollowed = new User()
                .withId(2L)
                .withUserStats(new UserStats());
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

        User fromUser = new User()
                .withId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.follow(1, 2));
    }

    @Test
    void shouldGetFollowing() {
        User user = new User()
                .withId(1L)
                .withUsername("username1")
                .withImage("url1")
                .withUserInfo(new UserInfo())
                .withUserStats(new UserStats());
        User followedUser = new User()
                .withId(2L)
                .withUsername("username2")
                .withImage("url2")
                .withUserInfo(new UserInfo())
                .withUserStats(new UserStats());
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
    void shouldGetFollowers() {
        User user = new User()
                .withId(1L)
                .withUsername("username1")
                .withImage("url1")
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());
        User userFollower = new User()
                .withId(2L)
                .withUsername("username2")
                .withImage("url2")
                .withUserStats(new UserStats())
                .withUserInfo(new UserInfo());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user
                .withFollowers(List.of(userFollower))));

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
        User user = new User()
                .withId(1L)
                .withUserStats(new UserStats());
        User followedUser = new User()
                .withId(2L)
                .withUserStats(new UserStats());
        user.addFollower(followedUser);
        followedUser.withFollowers(List.of(user));
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

        User fromUser = new User()
                .withId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.unfollow(1, 2));

    }

    @Test
    void shouldUpdatePoints() {
        User user = new User()
                .withId(1L)
                .withUserStats(new UserStats(1, 0,0,0,0,0,0));
        User userToSave = new User()
                .withId(1L)
                .withUserStats(new UserStats(1, 0,0,0,0,10,0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userToSave);
        User savedUser = socialService.updatePoints(1L, 10);
        assertEquals(10, savedUser.getUserStats().getPoints());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.updatePoints(2L, 10));
    }

    @Test
    void shouldUpdatePointsSubLessThan0() {
        User user = new User()
                .withId(1L)
                .withUserStats(new UserStats(1,0,0,0,0,3,0));
        User userToSave = new User()
                .withId(1L)
                .withUserStats(new UserStats(1,0,0,0,0,0,0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(userToSave);
        User savedUser = socialService.updatePoints(1L, -10);
        assertEquals(0, savedUser.getUserStats().getPoints());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.updatePoints(2L, 10));
    }

    @Test
    void shouldUpdateSocialLinks() {
        User user = new User()
                .withId(1L)
                .withUsername("username")
                .withUserInfo(new UserInfo(1, "ig", null, null, null, null));
        User returnUser = new User()
                .withId(1L)
                .withUsername("username")
                .withUserInfo(new UserInfo(1, "ig", "fb", null, null, null));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(argThat(argument ->
                argument.getUserInfo().getFb().equalsIgnoreCase("fb") &&
                argument.getUserInfo().getIg().equalsIgnoreCase("ig") &&
                argument.getUserInfo().getTwitter() == null)))
                .thenReturn(returnUser);
        Map<String, String> links = new LinkedHashMap<>();
        links.put("fb", "fb");
        links.put("ig", "ig");
        links.put("twitter", null);
        User savedUser = socialService.updateSocialLinks(1, links);
        assertEquals(1L, savedUser.getId());
        assertEquals("username", savedUser.getUsername());
        assertEquals("fb", savedUser.getUserInfo().getFb());
        assertEquals("ig", savedUser.getUserInfo().getIg());
        assertNull(user.getUserInfo().getTwitter());

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.updateSocialLinks(2L, Map.of()));
    }

    @Test
    void shouldSaveUserLocation() {
        User user = new User()
                .withId(1L)
                .withUserInfo(new UserInfo(1, "ig", null, null, null, null));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        socialService.saveUserLocation(1L, new Coordinates(53.21, 32.67));
        verify(userRepository, times(1))
                .save(argThat(arg ->
                        arg.getId() == 1L &&
                        arg.getUserInfo().getLongitude() == 53.21 &&
                        arg.getUserInfo().getLatitude() == 32.67));

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.saveUserLocation(2L, new Coordinates(32.12, 53.31)));
    }

}