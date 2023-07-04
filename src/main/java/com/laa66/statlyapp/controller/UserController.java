package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.ProfileDTO;
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

import java.util.List;

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

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/me/delete")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal OAuth2User principal) {
        userService.deleteUser((long) principal.getAttributes().get("userId"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/following")
    public ResponseEntity<FollowersDTO> getCurrentUserFollowing(@AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        FollowersDTO followersDTO = socialService.getFollowers(userId, StatlyConstants.FOLLOWING);
        return ResponseEntity.ok(followersDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUser(@RequestParam String username) {
        List<User> users = username.isBlank() ? List.of() : userService.findAllMatchingUsers(username);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/follow")
    public ResponseEntity<Void> follow(@AuthenticationPrincipal OAuth2User principal, @RequestParam("user_id") long followId) {
        long userId = (long) principal.getAttributes().get("userId");
        socialService.follow(userId, followId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unfollow")
    public ResponseEntity<Void> unfollow(@AuthenticationPrincipal OAuth2User principal, @RequestParam("user_id") long followId) {
        long userId = (long) principal.getAttributes().get("userId");
        socialService.unfollow(userId, followId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileDTO> getProfile(@RequestParam("user_id") long userId) {
        ProfileDTO userProfile = socialService.getUserProfile(userId);
        return ResponseEntity.ok(userProfile);
    }

}
