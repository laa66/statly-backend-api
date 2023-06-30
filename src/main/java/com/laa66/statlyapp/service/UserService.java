package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    String authenticateUser(UserDTO userDTO);

    Optional<User> findUserByEmail(String email);

    com.laa66.statlyapp.model.User findUserByUsername(String username);

    List<com.laa66.statlyapp.model.User> findAllMatchingUsers(String username);

    User saveUser(User user);

    void deleteUser(long id);

    void saveBetaUser(BetaUserDTO dto);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
