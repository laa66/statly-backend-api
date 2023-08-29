package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.model.OAuth2UserWrapper;
import com.laa66.statlyapp.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationDataController {

    private final LocationService locationService;

    @GetMapping("/users/matching")
    public ResponseEntity<Collection<UserDTO>> findClosestMatchingUsers(@AuthenticationPrincipal OAuth2UserWrapper principal) {
        Long userId = principal.getUserId();
        Collection<UserDTO> closestMatchingUsers = locationService.findBestMatchingUsers(userId);
        return ResponseEntity.ok(closestMatchingUsers);
    }

    @GetMapping("/users/nearby")
    public ResponseEntity<Collection<UserDTO>> findUsersNearby(@AuthenticationPrincipal OAuth2UserWrapper principal) {
        Long userId = principal.getUserId();
        Collection<UserDTO> usersNearby = locationService.findUsersNearby(userId);
        return ResponseEntity.ok(usersNearby);
    }

    @GetMapping("/token")
    public ResponseEntity<String> getMapAccessToken() {
        String mapAccessToken = locationService.getMapAccessToken();
        return ResponseEntity.ok(mapAccessToken);
    }

}
