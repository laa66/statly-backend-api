package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;

import java.util.Collection;
import java.util.List;

public interface MapAPIService {
    Collection<UserDTO> getDistanceMatrix(List<UserDTO> users);
    UserDTO getReverseGeocoding(UserDTO user);
}
