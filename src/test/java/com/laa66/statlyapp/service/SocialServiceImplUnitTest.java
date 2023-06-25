package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Artist;
import com.laa66.statlyapp.model.PlaylistInfo;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.impl.SocialServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SocialServiceImplUnitTest {

    @Mock
    UserRepository userRepository;

    @Mock
    StatsService statsService;

    @Mock
    SpotifyAPIService spotifyAPIService;

    @Mock
    LibraryAnalysisService libraryAnalysisService;

    @InjectMocks
    SocialServiceImpl socialService;

    @Test
    void shouldGetUserProfileValidUserId() {
        User user = new User(1L, "username","test@mail.com", "url", 0, LocalDateTime.of(2022, 11, 20, 20, 20));
        TracksDTO tracksDTO = new TracksDTO(List.of(new Track(List.of(new Artist("artist")), "title"))
                , "2", "long", null);
        ArtistsDTO artistsDTO = new ArtistsDTO("1", List.of(new Artist("artist"))
                , "long", null);
        ResponsePlaylists responsePlaylists = new ResponsePlaylists(null, 1, List.of(new PlaylistInfo()));
        LibraryAnalysisDTO libraryAnalysisDTO = new LibraryAnalysisDTO(Map.of("mainstream", 50.0), List.of());
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(statsService.getUserTracks(1, "long")).thenReturn(tracksDTO);
        when(statsService.getUserArtists(1, "long")).thenReturn(artistsDTO);
        when(spotifyAPIService.getUserPlaylists("username")).thenReturn(responsePlaylists);
        when(spotifyAPIService.getTopTracks(1, "long")).thenReturn(tracksDTO);
        when(libraryAnalysisService.getLibraryAnalysis(tracksDTO)).thenReturn(libraryAnalysisDTO);
        ProfileDTO profileDTO = socialService.getUserProfile(1, "username");
        assertEquals(1L, profileDTO.getId());
        assertEquals("username", profileDTO.getUsername());
        assertEquals("url", profileDTO.getImageUrl());
        assertEquals(0, profileDTO.getPoints());
        assertEquals(user.getJoinDate(), profileDTO.getJoinDate());
        assertEquals("title", profileDTO.getTopTracks().get(0).getName());
        assertEquals("artist", profileDTO.getTopArtists().get(0).getName());
        assertEquals(1, profileDTO.getUserPlaylists().size());
        assertEquals(50.0, profileDTO.getMainstream());
    }

    @Test
    void shouldGetUserProfileNotValidUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> socialService.getUserProfile(1L, "username"));
    }

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
