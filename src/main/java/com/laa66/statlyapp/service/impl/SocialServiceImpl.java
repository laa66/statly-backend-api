package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.UserProfileDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.repository.UserRepository;
import com.laa66.statlyapp.service.SocialService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Transactional
public class SocialServiceImpl implements SocialService {

    private final static Supplier<UserNotFoundException> USER_NOT_FOUND_EXCEPTION_SUPPLIER = () -> new UserNotFoundException("User not found");

    private final UserRepository userRepository;

    @Override
    public UserProfileDTO getUserProfile(long userId) {
        return null;
    }

    @Override
    public FollowersDTO getFollowers(long userId, StatlyConstants type) {
        return userRepository.findById(userId)
                .map(foundUser -> {
                    List<User> followers = type == StatlyConstants.FOLLOWING ? foundUser.getFollowing() : foundUser.getFollowers();
                    List<com.laa66.statlyapp.model.User> list = followers.stream()
                            .map(follower -> new com.laa66.statlyapp.model.User(
                                    Long.toString(follower.getId()),
                                    null,
                                    follower.getUsername(),
                                    List.of(new Image(follower.getImage(), null, null)))
                            ).toList();
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
}
