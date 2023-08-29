package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.*;

import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.mapper.EntityMapper;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDTO> findAllUsers() {
        Collection<User> users = (Collection<User>) userRepository.findAll();
        return users.stream()
                .map(EntityMapper::toUserDTO)
                .toList();
    }

    @Override
    public UserDTO findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(EntityMapper::toUserDTO)
                .orElse(null);
    }

    @Override
    public UserDTO findUserById(long userId) {
        return userRepository.findById(userId)
                .map(EntityMapper::toUserDTO)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public List<UserDTO> findAllMatchingUsers(String username) {
        return userRepository.findAllMatchingUsers(username).stream()
                .map(EntityMapper::toUserDTO)
                .toList();
    }

    @Override
    public List<UserDTO> findAllUsersOrderByPoints() {
        return userRepository.findAllUsersOrderByPoints().stream()
                .map(EntityMapper::toUserDTO)
                .toList();
    }

    @Override
    public UserDTO saveUser(User user) {
        User savedUser = userRepository.save(user);
        return EntityMapper.toUserDTO(savedUser);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.findById(id).ifPresentOrElse(user -> userRepository.deleteById(user.getId()),
                        () -> {
                    throw new UserNotFoundException("User not found");
                });
    }
}
