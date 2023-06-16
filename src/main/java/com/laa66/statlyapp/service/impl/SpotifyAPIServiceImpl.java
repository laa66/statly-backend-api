package com.laa66.statlyapp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.laa66.statlyapp.DTO.*;
import com.laa66.statlyapp.constants.SpotifyAPI;
import com.laa66.statlyapp.exception.SpotifyAPIEmptyResponseException;
import com.laa66.statlyapp.exception.SpotifyAPIException;
import com.laa66.statlyapp.model.*;
import com.laa66.statlyapp.model.request.RequestUpdatePlaylist;
import com.laa66.statlyapp.model.response.ResponsePlaylists;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class SpotifyAPIServiceImpl implements SpotifyAPIService {

    private static final Supplier<SpotifyAPIEmptyResponseException> SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER =
            () -> new SpotifyAPIEmptyResponseException("Empty Spotify API response", HttpStatus.NO_CONTENT.value());

    private final RestTemplate restTemplate;
    private final StatsService statsService;

    @Override
    public UserDTO getCurrentUser() {
        return restTemplate.exchange(SpotifyAPI.CURRENT_USER.get(), HttpMethod.GET, null, UserDTO.class).getBody();
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public TracksDTO getTopTracks(long userId, String range) {
        TracksDTO body = restTemplate.exchange(SpotifyAPI.TOP_TRACKS.get() + range + "_term", HttpMethod.GET, null, TracksDTO.class).getBody();
        return Optional.ofNullable(body).map(topTracksDTO -> {
            topTracksDTO.withRange(range);
            return statsService.compareTracks(userId, topTracksDTO);
        }).orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER);
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public ArtistsDTO getTopArtists(long userId, String range) {
        ArtistsDTO body = restTemplate.exchange(SpotifyAPI.TOP_ARTISTS.get() + range + "_term", HttpMethod.GET, null, ArtistsDTO.class).getBody();
        return Optional.ofNullable(body).map(topArtistsDTO -> {
                    topArtistsDTO.withRange(range);
                    return statsService.compareArtists(userId, topArtistsDTO);
        }).orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER);
    }

    @Override
    public RecentlyPlayedDTO getRecentlyPlayed() {
        return restTemplate.exchange(SpotifyAPI.RECENTLY_PLAYED_TRACKS.get(), HttpMethod.GET, null, RecentlyPlayedDTO.class).getBody();
    }

    @Override
    public ResponseTracksAnalysis getTracksAnalysis(TracksDTO tracksDTO) {
        return Optional.ofNullable(tracksDTO)
                .map(tracks -> {
                    ResponseTracksAnalysis tracksAnalysis = new ResponseTracksAnalysis(new LinkedList<>());
                    IntStream.iterate(0, i -> i < tracks.getTracks().size(), i -> i + 100)
                            .mapToObj(i -> tracks.getTracks().stream()
                                    .skip(i)
                                    .limit(100))
                            .map(trackStream -> getTracksIds(trackStream))
                            .map(ids -> SpotifyAPI.TRACKS_ANALYSIS.get() + ids)
                            .map(url -> restTemplate.exchange(url, HttpMethod.GET, null, ResponseTracksAnalysis.class).getBody())
                            .map(body -> Optional.ofNullable(body)
                                    .orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER)
                                    .getTracksAnalysis())
                            .forEach(tracksAnalysis::addAll);
                    return tracksAnalysis;
                }).orElseThrow(() -> new RuntimeException("Tracks cannot be null"));
    }

    private String getTracksIds(Stream<Track> trackStream) {
        return trackStream
                .map(Track::getId)
                .collect(Collectors.joining(","));
    }

    @Override
    public ResponsePlaylists getUserPlaylists() {
        String url = SpotifyAPI.USER_PLAYLISTS.get();
        ResponsePlaylists responsePlaylists = new ResponsePlaylists(null, 0, new LinkedList<>());
        ResponsePlaylists body;
        do {
            body = Optional
                    .ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, ResponsePlaylists.class).getBody())
                    .orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER);
            url = body.getNext();
            responsePlaylists.addAll(body.getPlaylists());
        } while (body.getNext() != null);
        responsePlaylists.setTotal(body.getTotal());
        return responsePlaylists;
    }

    @Override
    public TracksDTO getPlaylistTracks(PlaylistInfo playlistInfo, String country) {
        String url = SpotifyAPI.PLAYLIST_TRACKS.get().replace("playlist_id", playlistInfo.getId()).replace("country_code", country);
        Playlist playlist = new Playlist(playlistInfo.getName(), null, new LinkedList<>());
        Playlist body;
        do {
            body = Optional
                    .ofNullable(restTemplate.exchange(url, HttpMethod.GET, null, Playlist.class).getBody())
                    .orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER);
            url = body.getNext();
            playlist.addAll(body.getTracks());
        } while (body.getNext() != null);
        return toTracksDTO(playlist);
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
        List<String> uris = getTopTracks(userId, range).getTracks()
                .stream()
                .map(Track::getUri)
                .toList();
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
                .orElseThrow(SPOTIFY_API_EMPTY_RESPONSE_EXCEPTION_SUPPLIER);
    }

    private void postTracksToPlaylist(PlaylistDTO playlist, List<String> uris) {
        RequestUpdatePlaylist request = new RequestUpdatePlaylist(uris, 0);
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

    private TracksDTO toTracksDTO(Playlist playlist) {
        List<Track> tracks = playlist.getTracks()
                .stream()
                .map(PlaylistTrack::getTrack)
                .toList();
        return new TracksDTO(tracks, Integer.toString(tracks.size()), null, LocalDate.now());
    }
}
