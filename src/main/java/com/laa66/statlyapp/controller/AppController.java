package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.model.SpotifyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;

@RestController
public class AppController {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @GetMapping("/")
    public String home(OAuth2AuthenticationToken token) {
        return "hello!";
    }

}
