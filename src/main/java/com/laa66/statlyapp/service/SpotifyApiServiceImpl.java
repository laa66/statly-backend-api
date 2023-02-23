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
    public List<ItemTopTracks> getTopTracks(String url) {
        ResponseEntity<SpotifyResponseTopTracks> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopTracks.class);
        return response.getBody().getItemTopTracks();
    }

    @Override
    public List<ItemTopArtists> getTopArtists(String url) {
        ResponseEntity<SpotifyResponseTopArtists> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopArtists.class);
        System.out.println("Headers: " + response.getHeaders());
        System.out.println("Status code: " + response.getStatusCode());
        System.out.println("Body: " + response.getBody());
        return response.getBody().getItemTopArtists();
    }

    //use top tracks here and top artists
 /*
    @Override
    public List<Genre> getTopGenres() {
        return null;
    }
  */

    @Override
    public List<ItemRecentlyPlayed> getRecentlyPlayed() {
        ResponseEntity<SpotifyResponseRecentlyPlayed> response =
                restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, SpotifyResponseRecentlyPlayed.class);
        return response.getBody().getItems();
    }

    @Override
    public void postPlaylist() {

    }
}
