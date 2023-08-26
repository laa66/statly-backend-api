package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;

import java.util.Collection;

public interface LocationService {
    Collection<UserDTO> findBestMatchingUsers(long userId);
    Collection<UserDTO> findUsersNearby(long userId);
    UserDTO calculateDistanceHaversine(UserDTO user1, UserDTO user2);
}
