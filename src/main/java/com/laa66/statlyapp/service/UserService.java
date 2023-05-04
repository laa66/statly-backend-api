package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(String email);

    User saveUser(User user);

    void deleteUser(long id);

    void saveBetaUser(BetaUserDTO dto);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
