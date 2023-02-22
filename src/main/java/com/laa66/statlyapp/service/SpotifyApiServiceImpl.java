package com.laa66.statlyapp.service;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Item> getTopTracks(String url) {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return null;
    }

    @Override
    public List<Item> getTopArtists(String url) {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        return null;
    }

    //use top tracks here and top artists
 /*
    @Override
    public List<Genre> getTopGenres() {
        return null;
    }
  */

    @Override
    public List<Item> getRecentlyPlayed() {
        ResponseEntity<SpotifyResponse> response = restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, SpotifyResponse.class);
        return response.getBody().getItems();
    }

    @Override
    public void postPlaylist() {

    }
}
