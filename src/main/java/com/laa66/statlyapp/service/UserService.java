package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.model.User;


import java.util.List;

public interface UserService {

    String authenticateUser(UserDTO userDTO);

    User findUserByEmail(String email);

    User findUserById(long userId);

    List<User> findAllMatchingUsers(String username);

    List<User> findAllUsersOrderByPoints();

    User saveUser(com.laa66.statlyapp.entity.User user);

    void deleteUser(long id);

    void saveBetaUser(BetaUserDTO dto);

    List<BetaUserDTO> findAllBetaUsers();

    void deleteAllBetaUsers();

}
