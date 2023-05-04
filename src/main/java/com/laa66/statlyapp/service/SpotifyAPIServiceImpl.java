package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SpotifyAPIServiceImpl implements SpotifyAPIService {

    private final RestTemplate restTemplate;
    private final StatsService statsService;

    public SpotifyAPIServiceImpl(@Qualifier("restTemplateInterceptor") RestTemplate restTemplate, StatsService statsService) {
        this.restTemplate = restTemplate;
        this.statsService = statsService;
    }

    @Override
    public UserDTO getCurrentUser() {
        return restTemplate.exchange(SpotifyAPI.CURRENT_USER, HttpMethod.GET, null, UserDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopTracksDTO getTopTracks(long userId, String range) {
        log.info("TopTracksDTO " + range + " object for user: " + userId + " cached!");
        TopTracksDTO body = restTemplate.exchange(SpotifyAPI.TOP_TRACKS + range + "_term", HttpMethod.GET, null, TopTracksDTO.class).getBody();
        if (body != null) body.setRange(range);
        return statsService.compareTracks(userId, body);
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopArtistsDTO getTopArtists(long userId, String range) {
        log.info("TopArtistsDTO " + range + " object for user: " + userId + " cached!");
        TopArtistsDTO body = restTemplate.exchange(SpotifyAPI.TOP_ARTISTS + range + "_term", HttpMethod.GET, null, TopArtistsDTO.class).getBody();
        if (body != null) body.setRange(range);
        return statsService.compareArtists(userId, body);
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopGenresDTO getTopGenres(long userId, String range) {
        TopArtistsDTO response = getTopArtists(userId, range);
        List<ItemTopArtists> topArtists = response.getItemTopArtists();
        HashMap<String, Integer> map = new HashMap<>();
        topArtists.forEach(list -> list.getGenres().forEach(item -> {
                            map.merge(item, 1, (x,y) -> map.get(item)+1);}));
        List<Genre> genres = new ArrayList<>();
        map.forEach((x, y) -> genres.add(new Genre(x, y)));
        genres.sort(Comparator.reverseOrder());
        List<Genre> sliceGenres = genres.size() > 8 ? genres.subList(0, 8) : genres;
        double sum = sliceGenres.stream().mapToInt(Genre::getScore).sum();
        return statsService.compareGenres(userId, new TopGenresDTO(sliceGenres.stream()
                .map(item -> new Genre(item.getGenre(), Double.valueOf((item.getScore() / sum) * 100)
                        .intValue())).collect(Collectors.toList()), range));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public MainstreamScoreDTO getMainstreamScore(long userId, String range) {
        TopTracksDTO response = getTopTracks(userId, range);
        double result = response.getItemTopTracks().stream().mapToInt(ItemTopTracks::getPopularity)
                .average()
                .orElse(0);
        return statsService.compareMainstream(userId, new MainstreamScoreDTO(result, range));
    }

    @Override
    public RecentlyPlayedDTO getRecentlyPlayed() {
        return restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS, HttpMethod.GET, null, RecentlyPlayedDTO.class).getBody();
    }

    @Override
    public PlaylistDTO postTopTracksPlaylist(long userId, String range) {
        UserDTO user = getCurrentUser();
        String playlistRange;
        switch (range) {
            case "short" -> playlistRange = SpotifyAPI.RANGE_SHORT;
            case "medium" -> playlistRange = SpotifyAPI.RANGE_MEDIUM;
            case "long" -> playlistRange = SpotifyAPI.RANGE_LONG;
            default -> throw new SpotifyAPIException("Wrong data range", HttpStatus.BAD_REQUEST.value());
        }
        PlaylistDTO playlist = postEmptyPlaylist(user, playlistRange);
        List<String> uris = getTopTracks(userId, range).getItemTopTracks().stream().map(ItemTopTracks::getUri).toList();
        postTracksToPlaylist(playlist, uris);
        return playlist;
    }

    // helpers
    public PlaylistDTO postEmptyPlaylist(UserDTO user, String range) {
        String url = SpotifyAPI.CREATE_TOP_PLAYLIST.replace("user_id", user.getId());
        String body;
        try {
            Resource resource = new ClassPathResource("json/post-playlist.json");
            InputStream inputStream = resource.getInputStream();
            body = StreamUtils.copyToString(inputStream, Charset.defaultCharset());
            body = body.replaceAll("%range%", range)
                    .replace("%date%", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read post-playlist.json", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, PlaylistDTO.class).getBody();
    }

    public void postTracksToPlaylist(PlaylistDTO playlist, List<String> uris) {
        SpotifyRequestAddTracks request = new SpotifyRequestAddTracks(uris, 0);
        String body;
        try {
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
        restTemplate.exchange(SpotifyAPI.ADD_PLAYLIST_TRACK
                        .replace("playlist_id", playlist.getId()), HttpMethod.POST, new HttpEntity<>(body), String.class).getBody();
    }
}
