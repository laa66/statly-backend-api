package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.model.mapbox.Coordinates;
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
    public Collection<UserDTO> findBestMatchingUsers(long userId) {
        UserDTO baseUser = userService.findUserById(userId);
        return userService.findAllUsers()
                .stream()
                .filter(userDTO -> userId != Long.parseLong(userDTO.getId()))
                .filter(user -> user.getCoordinates() != null)
                .map(user -> user.withMatch(analysisService
                            .getUsersMatching(userId, Long.parseLong(user.getId()))
                            .getOrDefault("overall", null)))
                .sorted(Comparator.comparing(UserDTO::getMatch, Double::compareTo))
                .limit(10)
                .map(user -> mapAPIService
                        .getReverseGeocoding(calculateDistanceHaversine(baseUser, user)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    collected.add(0, baseUser);
                    return collected;
                }));
    }

    @Override
    public Collection<UserDTO> findUsersNearby(long userId) {
        UserDTO baseUser = userService.findUserById(userId);
        List<UserDTO> users = userService.findAllUsers()
                .stream()
                .filter(userDTO -> userId != Long.parseLong(userDTO.getId()))
                .filter(user -> user.getCoordinates() != null)
                .filter(baseUser::isNearby)
                .map(mapAPIService::getReverseGeocoding)
                .collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    collected.add(0, baseUser);
                    return collected;
                }));
        return users.size() > 1 ? mapAPIService.getDistanceMatrix(users) : users;
    }

    @Override
    public UserDTO calculateDistanceHaversine(UserDTO baseUser, UserDTO user) {
        Coordinates c1 = baseUser.getCoordinates();
        Coordinates c2 = user.getCoordinates();
        double earthRad = 6371;

        double dLon = (c2.getLongitude() - c1.getLongitude()) * Math.PI / 180;
        double dLat = (c2.getLatitude() - c1.getLatitude()) * Math.PI / 180;

        double haversine = Math.sin(dLat / 2)
                * Math.sin(dLat / 2)
                + Math.cos(c1.getLatitude()
                * Math.PI / 180)
                * Math.cos(c2.getLatitude()
                * Math.PI / 180)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double distance = earthRad * 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return user.withDistance(distance);
    }
}
