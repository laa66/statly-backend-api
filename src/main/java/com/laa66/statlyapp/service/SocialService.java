package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.FollowersDTO;

public interface SocialService {

    FollowersDTO getFollowed(long userId);

    FollowersDTO getFollowers(long userId);

    void follow(long userId, long followId);

    void unfollow(long userId, long unfollowId);

}
