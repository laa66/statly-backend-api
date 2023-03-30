package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SpotifyAPIServiceImpl implements SpotifyAPIService {

    @Autowired
    @Qualifier("restTemplateInterceptor")
    private RestTemplate restTemplate;

    @Override
    public UserIdDTO getCurrentUser() {
        System.out.println("Fire!");
        return restTemplate.exchange(SpotifyAPI.CURRENT_USER, HttpMethod.GET, null, UserIdDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public TopTracksDTO getTopTracks(String username, String url) {
        System.out.println("Fire!");
        return restTemplate.exchange(url, HttpMethod.GET, null, TopTracksDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public TopArtistsDTO getTopArtists(String username, String url) {
        System.out.println("Fire!");
        return restTemplate.exchange(url, HttpMethod.GET, null, TopArtistsDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public TopGenresDTO getTopGenres(String username, String url) {
        System.out.println("Fire!");
        TopArtistsDTO response = getTopArtists(username, url);
        if (response == null) return null;
        List<ItemTopArtists> topArtists = response.getItemTopArtists();
        HashMap<String, Integer> map = new HashMap<>();
        topArtists.forEach(list -> list.getGenres().forEach(item -> {
                            map.merge(item, 1, (x,y) -> map.get(item)+1);}));
        List<Genre> genres = new ArrayList<>();
        map.forEach((x, y) -> genres.add(new Genre(x, y)));
        genres.sort(Comparator.reverseOrder());
        List<Genre> sliceGenres = genres.size() > 8 ? genres.subList(0, 8) : genres;
        double sum = sliceGenres.stream().mapToInt(Genre::getScore).sum();
        return new TopGenresDTO(sliceGenres.stream()
                .map(item -> new Genre(item.getGenre(), Double.valueOf((item.getScore() / sum) * 100)
                        .intValue())).collect(Collectors.toList()));
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username + #url")
    public MainstreamScoreDTO getMainstreamScore(String username, String url) {
        System.out.println("Fire!");
        TopTracksDTO response = getTopTracks(username, url);
        if (response == null) return null;
        OptionalDouble result = response.getItemTopTracks().stream().mapToDouble(ItemTopTracks::getPopularity).average();
        MainstreamScoreDTO score = new MainstreamScoreDTO();
        score.setScore(result.getAsDouble());
        return score;
    }

    @Override
    @Cacheable(cacheNames = "api", key = "#root.methodName + #username")
    public RecentlyPlayedDTO getRecentlyPlayed(String username) {
        System.out.println("Fire!");
        return restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, RecentlyPlayedDTO.class).getBody();
    }

    @Override
    public String postTopTracksPlaylist(String username, String url) {
        UserIdDTO user = getCurrentUser();
        String range = null;
        if (url.endsWith("short_term")) range = SpotifyAPI.RANGE_SHORT;
        if (url.endsWith("medium_term")) range = SpotifyAPI.RANGE_MEDIUM;
        if (url.endsWith("long_term")) range = SpotifyAPI.RANGE_LONG;
        if (range == null) throw new SpotifyAPIException("Wrong data range", HttpStatus.BAD_REQUEST.value());
        UserIdDTO playlist = postEmptyPlaylist(user, range);
        List<String> uris = getTopTracks(username, url).getItemTopTracks().stream().map(ItemTopTracks::getUri).toList();
        return postTracksToPlaylist(playlist, uris);
    }

    // helpers
    @Scheduled(cron = "0 0 4 * * *")
    @CacheEvict(value = "api", allEntries = true)
    public void clearCache() {}

    public UserIdDTO postEmptyPlaylist(UserIdDTO user, String range) {
        String url = SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id", user.getId());
        String body;
        try {
            body = new String(Files.readAllBytes(Paths.get("src/main/resources/json/post-playlist.json")));
            body = body.replaceAll("%range%", range).
                    replace("%date%", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (IOException e) {
            throw new RuntimeException("I/O Exception while reading post-playlist.json");
        }
        return restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body), UserIdDTO.class).getBody();
    }

    public String postTracksToPlaylist(UserIdDTO playlist, List<String> uris) {
        SpotifyRequestAddTracks request = new SpotifyRequestAddTracks(uris, 0);
        String body;
        try {
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }

        return restTemplate.exchange(SpotifyAPI.ADD_PLAYLIST_TRACK
                        .replace("playlist_id", playlist.getId()), HttpMethod.POST, new HttpEntity<>(body), String.class).getBody();
    }
}
