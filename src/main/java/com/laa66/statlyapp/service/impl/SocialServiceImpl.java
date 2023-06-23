package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.UserProfileDTO;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getUserProfile(long userId) {
        return null;
    }

    @Override
    public FollowersDTO getFollowed(long userId) {
        return null;
    }

    @Override
    public FollowersDTO getFollowers(long userId) {
        return null;
    }

    @Override
    public User follow(long userId, long followId) {
        User user = userRepository.findById(userId)
                .map(foundUser -> {
                    foundUser.addFollowed(
                            userRepository.findById(followId).orElseThrow(() -> new UserNotFoundException("User not found"))
                            );
                    return foundUser;
                }).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userRepository.save(user);
    }

    @Override
    public void unfollow(long userId, long unfollowId) {

    }
}
