package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.UserProfileDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;

public interface SocialService {

    UserProfileDTO getUserProfile(long userId);

    FollowersDTO getFollowers(long userId, StatlyConstants type);

    User follow(long userId, long followId);

    void unfollow(long userId, long unfollowId);

}
