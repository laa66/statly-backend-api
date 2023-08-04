package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.User;


import java.util.List;

public interface UserService {

    UserDTO findUserByEmail(String email);

    UserDTO findUserById(long userId);

    List<UserDTO> findAllMatchingUsers(String username);

    List<UserDTO> findAllUsersOrderByPoints();

    UserDTO saveUser(User user);

    void deleteUser(long id);

}
