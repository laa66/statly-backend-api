package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.DTO.BetaUserDTO;
import com.laa66.statlyapp.service.BetaUserService;
import com.laa66.statlyapp.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/beta")
@RequiredArgsConstructor
public class BetaController {

    private final BetaUserService betaUserService;
    private final MailService mailService;

    @GetMapping("/all")
    public ResponseEntity<List<BetaUserDTO>> getAllBetaUsers() {
        List<BetaUserDTO> dto = betaUserService.findAllBetaUsers();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/join")
    public ResponseEntity<Void> join(@RequestParam("name") String fullName, @RequestParam String email) {
        betaUserService.saveBetaUser(new BetaUserDTO(fullName, email, null));
        mailService.sendJoinBetaNotification();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/notification")
    public ResponseEntity<Void> sendNotification(@RequestBody BetaUserDTO betaUserDTO) {
        mailService.sendAccessGrantedNotification(betaUserDTO);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAllBetaUsers() {
        betaUserService.deleteAllBetaUsers();
        return ResponseEntity.noContent().build();
    }
}
