package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.FollowersDTO;
import com.laa66.statlyapp.DTO.ProfileDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.constants.StatlyConstants;
import com.laa66.statlyapp.service.SocialService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SocialService socialService;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        long userId = (long) principal.getAttributes().get("userId");
        UserDTO userDTO = userService.findUserById(userId);
        return ResponseEntity.ok(userDTO);
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
    public ResponseEntity<List<UserDTO>> searchUser(@RequestParam String username) {
        List<UserDTO> userDTOList = username.isBlank() ? List.of() : userService.findAllMatchingUsers(username);
        return ResponseEntity.ok(userDTOList);
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

    @GetMapping("/rank")
    public ResponseEntity<List<UserDTO>> getRank() {
        List<UserDTO> userDTOList = userService.findAllUsersOrderByPoints();
        return ResponseEntity.ok(userDTOList);
    }

    @PutMapping("/links")
    public ResponseEntity<Void> addLinks(@AuthenticationPrincipal OAuth2User principal, @Valid @RequestBody Map<String, String> socialLinks) {
        long userid = (long) principal.getAttributes().get("userId");
        socialService.updateSocialLinks(userid, socialLinks);
        return ResponseEntity.noContent().build();
    }

}
