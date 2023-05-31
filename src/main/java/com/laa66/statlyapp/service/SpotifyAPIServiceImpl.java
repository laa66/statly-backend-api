package com.laa66.statlyapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIEmptyResponseException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SpotifyAPIServiceImpl implements SpotifyAPIService {

    private final RestTemplate restTemplate;
    private final StatsService statsService;

    @Override
    public UserDTO getCurrentUser() {
        return restTemplate.exchange(SpotifyAPI.CURRENT_USER.get(), HttpMethod.GET, null, UserDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopTracksDTO getTopTracks(long userId, String range) {
        TopTracksDTO body = restTemplate.exchange(SpotifyAPI.TOP_TRACKS.get() + range + "_term", HttpMethod.GET, null, TopTracksDTO.class).getBody();
        return Optional.ofNullable(body).map(topTracksDTO -> {
            topTracksDTO.withRange(range);
            return statsService.compareTracks(userId, topTracksDTO);
        }).orElseThrow(() -> new SpotifyAPIEmptyResponseException("Empty Spotify API response", HttpStatus.NO_CONTENT.value()));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopArtistsDTO getTopArtists(long userId, String range) {
        TopArtistsDTO body = restTemplate.exchange(SpotifyAPI.TOP_ARTISTS.get() + range + "_term", HttpMethod.GET, null, TopArtistsDTO.class).getBody();
        return Optional.ofNullable(body).map(topArtistsDTO -> {
                    topArtistsDTO.withRange(range);
                    return statsService.compareArtists(userId, topArtistsDTO);
        }).orElseThrow(() ->  new SpotifyAPIEmptyResponseException("Empty Spotify API response", HttpStatus.NO_CONTENT.value()));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TopGenresDTO getTopGenres(long userId, String range) {
        TopArtistsDTO response = getTopArtists(userId, range);
        List<ItemTopArtists> topArtists = response.getItemTopArtists();
        List<Genre> sliceGenres = topArtists
                .stream()
                .flatMap(artist -> artist.getGenres().stream())
                .collect(Collectors.collectingAndThen(Collectors.toMap(Function.identity(), genre -> 1, Integer::sum),
                        stringIntegerMap -> stringIntegerMap.entrySet()
                                .stream()
                                .map(entry -> new Genre(entry.getKey(), entry.getValue()))
                                .sorted(Comparator.reverseOrder())
                                .limit(10)
                                .toList()
                        ));
        double sum = sliceGenres
                .stream()
                .mapToInt(Genre::getScore)
                .sum();
        List<Genre> transformedGenres = sliceGenres.stream()
                .map(item -> new Genre(item.getGenre(), (int) ((item.getScore() / sum) * 100)))
                .collect(Collectors.toList());
        return statsService.compareGenres(userId, new TopGenresDTO(transformedGenres, range, null));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public MainstreamScoreDTO getMainstreamScore(long userId, String range) {
        TopTracksDTO response = getTopTracks(userId, range);
        double result = response.getItemTopTracks().stream()
                .mapToInt(ItemTopTracks::getPopularity)
                .average()
                .orElse(0);
        return statsService.compareMainstream(userId, new MainstreamScoreDTO(result, range, 0, null));
    }

    @Override
    public RecentlyPlayedDTO getRecentlyPlayed() {
        return restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get(), HttpMethod.GET, null, RecentlyPlayedDTO.class).getBody();
    }

    @Override
    public PlaylistDTO postTopTracksPlaylist(long userId, String range) {
        UserDTO user = getCurrentUser();
        String playlistRange;
        switch (range) {
            case "short" -> playlistRange = SpotifyAPI.PLAYLIST_RANGE_SHORT.get();
            case "medium" -> playlistRange = SpotifyAPI.PLAYLIST_RANGE_MEDIUM.get();
            case "long" -> playlistRange = SpotifyAPI.PLAYLIST_RANGE_LONG.get();
            default -> throw new SpotifyAPIException("Wrong data range", HttpStatus.BAD_REQUEST.value());
        }
        PlaylistDTO playlist = postEmptyPlaylist(user, playlistRange);
        List<String> uris = getTopTracks(userId, range).getItemTopTracks().stream().map(ItemTopTracks::getUri).toList();
        postTracksToPlaylist(playlist, uris);
        putPlaylistImage(playlist, range);
        return playlist;
    }

    // helpers
    private PlaylistDTO postEmptyPlaylist(UserDTO user, String range) {
        String url = SpotifyAPI.CREATE_TOP_PLAYLIST.get().replace("user_id", user.getId());
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
        PlaylistDTO playlist = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(body, headers), PlaylistDTO.class).getBody();
        return Optional.ofNullable(playlist)
                .orElseThrow(() -> new SpotifyAPIEmptyResponseException("Empty Spotify API response", HttpStatus.NO_CONTENT.value()));
    }

    private void postTracksToPlaylist(PlaylistDTO playlist, List<String> uris) {
        SpotifyRequestAddTracks request = new SpotifyRequestAddTracks(uris, 0);
        String body;
        try {
            ObjectMapper mapper = new ObjectMapper();
            body = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        restTemplate.exchange(SpotifyAPI.ADD_PLAYLIST_TRACK.get()
                        .replace("playlist_id", playlist.getId()), HttpMethod.POST, new HttpEntity<>(body), String.class);
    }

    private void putPlaylistImage(PlaylistDTO playlist, String range) {
        String encodedImage;
        try {
            Resource resource = new ClassPathResource("image/" + range + ".jpg");
            byte[] fileBytes = resource.getInputStream().readAllBytes();
            encodedImage = Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image", e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        restTemplate.exchange(SpotifyAPI.EDIT_PLAYLIST_IMAGE.get().replace("playlist_id", playlist.getId()), HttpMethod.PUT,
                new HttpEntity<>(encodedImage, headers), Void.class);
    }
}
