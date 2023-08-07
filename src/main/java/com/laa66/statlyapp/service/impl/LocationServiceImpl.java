package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.LocationService;
import com.laa66.statlyapp.service.MapAPIService;
import com.laa66.statlyapp.service.UserService;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final MapAPIService mapAPIService;
    private final UserService userService;
    private final LibraryAnalysisService analysisService;

    @Override
    public Collection<UserDTO> findClosestMatchingUsers(long userId) {
        UserDTO baseUser = userService.findUserById(userId);
        List<UserDTO> users = userService.findAllUsers()
                .stream()
                .filter(userDTO -> userId != Long.parseLong(userDTO.getId()))
                .map(user -> user.withMatch(analysisService
                            .getUsersMatching(userId, Long.parseLong(user.getId()))
                            .getOrDefault("overall", null)))
                .sorted(Comparator.comparing(UserDTO::getMatch, Double::compareTo))
                .limit(10)
                .map(mapAPIService::getReverseGeocoding)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    collected.add(0, baseUser);
                    return collected;
                }));

        return mapAPIService.getDistanceMatrix(users);
    }

    @Override
    public Collection<UserDTO> findUsersNearby(long userId) {
        UserDTO baseUser = userService.findUserById(userId);
        List<UserDTO> users = userService.findAllUsers()
                .stream()
                .filter(userDTO -> userId != Long.parseLong(userDTO.getId()) && baseUser.isNearby(userDTO))
                .map(mapAPIService::getReverseGeocoding)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    collected.add(0, baseUser);
                    return collected;
                }));
        return mapAPIService.getDistanceMatrix(users);
    }
}
