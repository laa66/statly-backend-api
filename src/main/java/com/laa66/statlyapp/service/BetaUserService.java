package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;

import java.util.List;

public interface BetaUserService {

    BetaUserDTO findBetaUserByEmail(String email);

    void saveBetaUser(BetaUserDTO betaUserDTO);

    boolean existsByEmail(String email);

    void activateUser(String email);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
