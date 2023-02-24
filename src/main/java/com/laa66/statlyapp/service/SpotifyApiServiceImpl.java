package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.exchange.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SpotifyApiServiceImpl implements SpotifyApiService {

    @Autowired
    private OAuth2AuthorizedClientService clientService;

    @Autowired
    private RestTemplate restTemplate;

    @Value("classpath:json/post-playlist.json")
    private Resource postPlaylistJson;

    @Override
    public SpotifyResponseId getCurrentUser() {
        ResponseEntity<SpotifyResponseId> responseUser = restTemplate.exchange(SpotifyAPI.CURRENT_USER, HttpMethod.GET, null, SpotifyResponseId.class);
        return responseUser.getBody();
    }

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

    @Override
    public SpotifyResponseTopGenres getTopGenres(String url) {
        ResponseEntity<SpotifyResponseTopArtists> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopArtists.class);
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
    public SpotifyResponseMainstreamScore getMainstreamScore(String url) {
        ResponseEntity<SpotifyResponseTopTracks> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopTracks.class);
        OptionalDouble result = response.getBody().getItemTopTracks().stream().mapToDouble(ItemTopTracks::getPopularity).average();
        SpotifyResponseMainstreamScore score = new SpotifyResponseMainstreamScore();
        score.setScore(result.getAsDouble());
        return score;
    }

    @Override
    public SpotifyResponseRecentlyPlayed getRecentlyPlayed() {
        ResponseEntity<SpotifyResponseRecentlyPlayed> response =
                restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, SpotifyResponseRecentlyPlayed.class);
        return response.getBody();
    }

    @Override
    public String postTopTracksPlaylist(String url) {
        SpotifyResponseId user = getCurrentUser();
        String range;
        switch (url) {
            case SpotifyAPI.TOP_TRACKS_SHORT -> range = SpotifyAPI.RANGE_SHORT;
            case SpotifyAPI.TOP_TRACKS_MEDIUM -> range = SpotifyAPI.RANGE_MEDIUM;
            case SpotifyAPI.TOP_TRACKS_LONG -> range = SpotifyAPI.RANGE_LONG;
            default -> throw new RuntimeException();
        }
        SpotifyResponseId playlist = postEmptyPlaylist(user, range);
        List<String> uris = getTopTracks(url).getItemTopTracks().stream().map(ItemTopTracks::getUri).toList();
        return postTracksToPlaylist(playlist, uris);
    }

    // helpers
    private SpotifyResponseId postEmptyPlaylist(SpotifyResponseId user, String range) {
        String url = SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id", user.getId());
        String body;
        try {
            body = Files.readString(postPlaylistJson.getFile().toPath());
            body = body.replaceAll("%range%", range).
                    replace("%date%", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (IOException e) {
            throw new RuntimeException();
        }
        ResponseEntity<SpotifyResponseId> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body), SpotifyResponseId.class);
        return response.getBody();
    }

    private String postTracksToPlaylist(SpotifyResponseId playlist, List<String> uris) {
        SpotifyRequestAddTracks request = new SpotifyRequestAddTracks(uris, 0);
        String body;
        try {
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }

        ResponseEntity<String> response =
                restTemplate.exchange(SpotifyAPI.ADD_PLAYLIST_TRACK
                        .replace("playlist_id", playlist.getId()), HttpMethod.POST, new HttpEntity<>(body), String.class);
        return response.getBody();
    }
}
