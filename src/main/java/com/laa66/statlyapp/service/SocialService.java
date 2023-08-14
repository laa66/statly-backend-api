package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.ProfileDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.entity.User;

import java.util.Map;

public interface SocialService {

    ProfileDTO getUserProfile(long userId);

    FollowersDTO getFollowers(long userId, StatlyConstants type);

    User follow(long userId, long followId);

    User unfollow(long userId, long unfollowId);

    User updatePoints(long userId, int points);

    User updateSocialLinks(long userId, Map<String, String> socialLinks);

    void saveUserLocation(long userId, Double longitude, Double latitude);

}
