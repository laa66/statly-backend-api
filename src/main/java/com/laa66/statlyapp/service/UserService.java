package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    String authenticateUser(UserDTO userDTO);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    User saveUser(User user);

    void deleteUser(long id);

    void saveBetaUser(BetaUserDTO dto);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
