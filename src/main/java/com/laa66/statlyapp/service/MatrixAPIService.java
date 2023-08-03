package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;

import java.util.Collection;

public interface MatrixAPIService {
    Collection<UserDTO> getUsersDistance(UserDTO user, Collection<UserDTO> usersToCheck);
}
