package com.laa66.statlyapp.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        System.out.println(authentication);
        return "Hello!";
    }

}
