package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.entity.BetaUser;
import com.laa66.statlyapp.exception.UserNotFoundException;
import com.laa66.statlyapp.repository.BetaUserRepository;
import com.laa66.statlyapp.service.BetaUserService;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class BetaUserServiceImpl implements BetaUserService {

    private final BetaUserRepository betaUserRepository;

    @Override
    public void saveBetaUser(BetaUserDTO betaUserDTO) {
        BetaUser betaUser = new BetaUser(0, betaUserDTO.getFullName(), betaUserDTO.getEmail(), LocalDateTime.now(), false);
        betaUserRepository.save(betaUser);
    }

    @Override
    public void activateUser(String email) {
        betaUserRepository.findByEmail(email)
                .ifPresentOrElse(betaUser -> {
                    betaUser.setActive(true);
                    betaUserRepository.save(betaUser);
                }, () -> {
                    throw new UserNotFoundException("Beta user not found");
                });
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
