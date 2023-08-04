package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;

import java.util.Collection;

public interface LocationService {
    Collection<UserDTO> findClosestMatchingUsers(long userId);
    Collection<UserDTO> findUsersNearby(long userId);
}
