package com.laa66.statlyapp.service;

import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.entity.*;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BetaUserRepository betaUserRepository;

    @Value("${api.react-app.url}")
    private final String reactUrl;

    @Override
    public String authenticateUser(UserDTO userDTO) {
        String imageUrl = userDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        return reactUrl + "/callback?name=" + StringUtils.stripAccents(userDTO.getDisplayName()) + "&url=" + (imageUrl.equals("none") ? "./account.png"  : imageUrl);

    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(long id) {
        userRepository.findById(id).ifPresentOrElse(item -> userRepository.deleteById(item.getId()), () -> {
                    throw new UserNotFoundException("User not found");
                });
    }

    @Override
    public void saveBetaUser(BetaUserDTO dto) {
        BetaUser betaUser = new BetaUser(0, dto.getFullName(), dto.getEmail(), LocalDateTime.now());
        betaUserRepository.save(betaUser);
    }

    @Override
    public List<BetaUserDTO> findAllBetaUsers() {
        return ((Collection<BetaUser>) betaUserRepository.findAll()).stream()
                .map(item -> new BetaUserDTO(item.getFullName(), item.getEmail(), item.getDate().toString()))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAllBetaUsers() {
        betaUserRepository.deleteAll();
    }
}
