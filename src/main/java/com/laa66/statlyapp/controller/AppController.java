package com.laa66.statlyapp.controller;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.ItemTopTracks;
import com.laa66.statlyapp.service.SpotifyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AppController {

    @Autowired
    private SpotifyApiService spotifyApiService;

    @GetMapping("/")
    public List<ItemTopTracks> home() {
        return spotifyApiService.getTopTracks(SpotifyAPI.TOP_TRACKS_SHORT);
    }

}
