package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.exchange.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
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
    private RestTemplate restTemplate;

    @Value("classpath:json/post-playlist.json")
    private Resource postPlaylistJson;

    @Override
    public SpotifyResponseId getCurrentUser() {
        ResponseEntity<SpotifyResponseId> responseUser = restTemplate.exchange(SpotifyAPI.CURRENT_USER, HttpMethod.GET, null, SpotifyResponseId.class);
        return responseUser.getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public SpotifyResponseTopTracks getTopTracks(String username, String url) {
        System.out.println("Fire!");
        ResponseEntity<SpotifyResponseTopTracks> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopTracks.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public SpotifyResponseTopArtists getTopArtists(String username, String url) {
        System.out.println("Fire!");
        ResponseEntity<SpotifyResponseTopArtists> response =
                restTemplate.exchange(url, HttpMethod.GET, null, SpotifyResponseTopArtists.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public SpotifyResponseTopGenres getTopGenres(String username, String url) {
        System.out.println("Fire!");
        SpotifyResponseTopArtists response = getTopArtists(username, url);
        if (response == null) return null;
        List<ItemTopArtists> topArtists = response.getItemTopArtists();
        HashMap<String, Integer> map = new HashMap<>();
        topArtists.forEach(list -> list.getGenres()
                        .forEach(item -> {
                            map.merge(item, 1, (x,y) -> map.get(item)+1);
                        }));
        SpotifyResponseTopGenres spotifyResponseTopGenres = new SpotifyResponseTopGenres();
        spotifyResponseTopGenres.setGenres(map);
        return spotifyResponseTopGenres;
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public SpotifyResponseMainstreamScore getMainstreamScore(String username, String url) {
        System.out.println("Fire!");
        SpotifyResponseTopTracks response = getTopTracks(username, url);
        if (response == null) return null;
        OptionalDouble result = response.getItemTopTracks().stream().mapToDouble(ItemTopTracks::getPopularity).average();
        SpotifyResponseMainstreamScore score = new SpotifyResponseMainstreamScore();
        score.setScore(result.getAsDouble());
        return score;
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username")
    public SpotifyResponseRecentlyPlayed getRecentlyPlayed(String username) {
        System.out.println("Fire!");
        ResponseEntity<SpotifyResponseRecentlyPlayed> response =
                restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, SpotifyResponseRecentlyPlayed.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    @Override
    public String postTopTracksPlaylist(String username, String url) {
        SpotifyResponseId user = getCurrentUser();
        String range = null;
        if (url.endsWith("short_term")) range = SpotifyAPI.RANGE_SHORT;
        if (url.endsWith("medium_term")) range = SpotifyAPI.RANGE_MEDIUM;
        if (url.endsWith("long_term")) range = SpotifyAPI.RANGE_LONG;
        if (range == null) throw new RuntimeException();
        SpotifyResponseId playlist = postEmptyPlaylist(user, range);
        List<String> uris = getTopTracks(username, url).getItemTopTracks().stream().map(ItemTopTracks::getUri).toList();
        return postTracksToPlaylist(playlist, uris);
    }


    // helpers
    @Scheduled(cron = "0 0 4 * * *")
    @CacheEvict(value = "api", allEntries = true)
    public void clearCache() {}

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
        return response.getStatusCode() == HttpStatus.CREATED ? response.getBody() : null;
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
        return response.getStatusCode() == HttpStatus.CREATED ? response.getBody() : null;
    }
}
