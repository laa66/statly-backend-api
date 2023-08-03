package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.UserDTO;

import java.util.Collection;
import java.util.List;

public interface MatrixAPIService {
    Collection<UserDTO> getDistanceMatrix(List<UserDTO> users);
}
