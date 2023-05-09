package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.DTO.UserDTO;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.service.MailService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final SpotifyAPIService spotifyApiService;
    private final MailService mailService;
    private final String reactUrl;

    public UserController(UserService userService, SpotifyAPIService spotifyApiService, MailService mailService, @Value("${api.react-app.url}") String reactUrl) {
        this.userService = userService;
        this.spotifyApiService = spotifyApiService;
        this.mailService = mailService;
        this.reactUrl = reactUrl;
    }

    @GetMapping("/auth")
    public void authenticate(HttpServletRequest request, HttpServletResponse response) {
        UserDTO userDTO = spotifyApiService.getCurrentUser();
        String imageUrl = userDTO.getImages().stream()
                .findFirst()
                .map(Image::getUrl)
                .orElse("none");
        String redirectUrl = reactUrl + "/callback?name=" + StringUtils.stripAccents(userDTO.getDisplayName()) + "&url=" + (imageUrl.equals("none") ? "./account.png"  : imageUrl);
        response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
        response.setHeader(HttpHeaders.LOCATION, redirectUrl);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal OAuth2User principal) {
        userService.deleteUser((long) principal.getAttributes().get("userId"));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/beta/all")
    public ResponseEntity<List<BetaUserDTO>> findAllBetaUsers() {
        List<BetaUserDTO> dto = userService.findAllBetaUsers();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/beta/join")
    public ResponseEntity<Void> join(@RequestBody BetaUserDTO betaUserDTO) {
        userService.saveBetaUser(betaUserDTO);
        mailService.sendJoinBetaNotification();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/beta/notification")
    public ResponseEntity<Void> sendNotification(@RequestBody BetaUserDTO betaUserDTO) {
        mailService.sendAccessGrantedNotification(betaUserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/beta/delete")
    public ResponseEntity<Void> deleteAllBetaUsers() {
        userService.deleteAllBetaUsers();
        return ResponseEntity.noContent().build();
    }

}
