package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.ProfileDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Transactional
public class SocialServiceImpl implements SocialService {

    private final static Supplier<UserNotFoundException> USER_NOT_FOUND_EXCEPTION_SUPPLIER = () -> new UserNotFoundException("User not found");

    private final UserRepository userRepository;
    private final StatsService statsService;
    private final SpotifyAPIService spotifyAPIService;
    private final LibraryAnalysisService libraryAnalysisService;

    @Override
    public ProfileDTO getUserProfile(long userId, String spotifyUserId) {
        return userRepository.findById(userId)
                .map(foundUser -> createProfileDTO(foundUser, userId, spotifyUserId))
                .orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Override
    public FollowersDTO getFollowers(long userId, StatlyConstants type) {
        return userRepository.findById(userId)
                .map(foundUser -> {
                    List<User> followers = type == StatlyConstants.FOLLOWING ? foundUser.getFollowing() : foundUser.getFollowers();
                    List<com.laa66.statlyapp.model.User> list = followers.stream()
                            .map(User::toModelUser)
                            .toList();
                    return new FollowersDTO(list.size(), list);
                }).orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER);
    }

    @Override
    public User follow(long userId, long followId) {
        User user = userRepository.findById(userId)
                .map(foundUser -> {
                    foundUser.addFollower(
                            userRepository.findById(followId)
                            .orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER)
                    );
                    return foundUser;
                }).orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER);
        return userRepository.save(user);
    }

    @Override
    public User unfollow(long userId, long unfollowId) {
        User user = userRepository.findById(userId)
                .map(foundUser -> {
                    foundUser.removeFollower(
                            userRepository.findById(unfollowId)
                            .orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER)
                    );
                    return foundUser;
                }).orElseThrow(USER_NOT_FOUND_EXCEPTION_SUPPLIER);
        return userRepository.save(user);
    }

    //helpers
    private ProfileDTO createProfileDTO(User user, long userId, String spotifyUserId) {
        return new ProfileDTO(
                user.getId(),
                user.getUsername(),
                user.getImage(),
                user.getPoints(),
                user.getJoinDate(),
                user.getFollowing().stream()
                        .map(User::toModelUser)
                        .toList(),
                user.getFollowers().stream()
                        .map(User::toModelUser)
                        .toList(),
                statsService.getUserTracks(userId, "long").getTracks(),
                statsService.getUserArtists(userId, "long").getArtists(),
                spotifyAPIService.getUserPlaylists(spotifyUserId).getPlaylists(),
                libraryAnalysisService.getLibraryAnalysis(spotifyAPIService.getTopTracks(userId, "long"))
                        .getLibraryAnalysis()
                        .get("mainstream")
        );
    }
}
