package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.service.MailService;
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
    private final MailService mailService;


    @GetMapping("/auth")
    public void authenticate(HttpServletRequest request, HttpServletResponse response) {
        String redirectUrl = userService.authenticateUser(spotifyApiService.getCurrentUser());
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

    @GetMapping("/beta/join")
    public ResponseEntity<Void> join(@RequestParam("name") String fullName, @RequestParam("email") String email) {
        userService.saveBetaUser(new BetaUserDTO(fullName, email, null));
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
