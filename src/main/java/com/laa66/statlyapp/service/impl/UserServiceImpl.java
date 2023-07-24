package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.*;

import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.entity.User;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.mapper.EntityMapper;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.repository.*;
import com.laa66.statlyapp.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BetaUserRepository betaUserRepository;

    @Value("${api.react-app.url}")
    private final String reactUrl;

    @Override
    public String authenticateUser(UserDTO userDTO, long userId) {
        String imageUrl = userDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        return reactUrl + "/statly-frontend/#/callback?name=" +
                StringUtils.stripAccents(userDTO.getName()) + "&url=" +
                (imageUrl.equals("none") ? "./account.png"  : imageUrl) +
                "&user_id=" + userId;
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

    @Override
    public void saveBetaUser(BetaUserDTO betaUserDTO) {
        BetaUser betaUser = new BetaUser(0, betaUserDTO.getFullName(), betaUserDTO.getEmail(), LocalDateTime.now());
        betaUserRepository.save(betaUser);
    }

    @Override
    public List<BetaUserDTO> findAllBetaUsers() {
        return ((Collection<BetaUser>) betaUserRepository.findAll()).stream()
                .map(user -> new BetaUserDTO(user.getFullName(), user.getEmail(), user.getDate().toString()))
                .toList();
    }

    @Override
    public void deleteAllBetaUsers() {
        betaUserRepository.deleteAll();
    }
}
