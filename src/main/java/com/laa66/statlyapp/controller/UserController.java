package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.model.User;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SpotifyAPIService spotifyApiService;
    private final SocialService socialService;

    @GetMapping("/auth")
    public void authenticate(HttpServletRequest request, HttpServletResponse response) {
        String redirectUrl = userService.authenticateUser(spotifyApiService.getCurrentUser());
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal OAuth2User principal) {
        userService.deleteUser((long) principal.getAttributes().get("userId"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/get")
    public ResponseEntity<User> getUser(@RequestParam String username) {
        User user = userService.findUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/following")
    public ResponseEntity<FollowersDTO> getUserFollowing(@AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        FollowersDTO followersDTO = socialService.getFollowers(userId, StatlyConstants.FOLLOWING);
        return ResponseEntity.ok(followersDTO);
    }

    @PutMapping("/follow")
    public ResponseEntity<Void> follow(@AuthenticationPrincipal OAuth2User principal, @RequestParam long followId) {
        long userId = (long) principal.getAttributes().get("userId");
        socialService.follow(userId, followId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unfollow")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal OAuth2User principal, @RequestParam long followId) {
        long userId = (long) principal.getAttributes().get("userId");
        socialService.unfollow(userId, followId);
        return ResponseEntity.noContent().build();
    }



}
