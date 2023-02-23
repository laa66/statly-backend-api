package com.laa66.statlyapp.service;

import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SpotifyResponseTopTracks getTopTracks(String url) {
        ResponseEntity<SpotifyResponseTopTracks> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopTracks.class);
        return response.getBody();
    }

    @Override
    public SpotifyResponseTopArtists getTopArtists(String url) {
        ResponseEntity<SpotifyResponseTopArtists> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopArtists.class);
        return response.getBody();
    }

    // Used User top artists endpoint here for calculating top genres
    @Override
    public SpotifyResponseTopGenres getTopGenres(String url) {
        ResponseEntity<SpotifyResponseTopArtists> response = restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopArtists.class);
        List<ItemTopArtists> topArtists = response.getBody().getItemTopArtists();
        HashMap<String, Integer> map = new HashMap<>();
        topArtists.forEach(list -> list.getGenres()
                        .forEach(item -> {
                            map.merge(item, 1, (x,y) -> map.get(item)+1);
                        }));
        map.entrySet().stream().sorted(Map.Entry.comparingByValue());
        SpotifyResponseTopGenres spotifyResponseTopGenres = new SpotifyResponseTopGenres();
        spotifyResponseTopGenres.setGenres(map);
        return spotifyResponseTopGenres;
    }


    @Override
    public SpotifyResponseRecentlyPlayed getRecentlyPlayed() {
        ResponseEntity<SpotifyResponseRecentlyPlayed> response =
                restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, SpotifyResponseRecentlyPlayed.class);
        return response.getBody();
    }

    @Override
    public void postPlaylist() {

    }
}
