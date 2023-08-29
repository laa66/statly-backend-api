package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.BetaUserDTO;

import java.util.List;

public interface BetaUserService {

    void saveBetaUser(BetaUserDTO betaUserDTO);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
