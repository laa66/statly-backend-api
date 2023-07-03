package com.laa66.statlyapp.service.impl;

import com.laa66.statlyapp.DTO.LibraryAnalysisDTO;
import com.laa66.statlyapp.DTO.ArtistsDTO;
import com.laa66.statlyapp.DTO.GenresDTO;
import com.laa66.statlyapp.DTO.TracksDTO;
import com.laa66.statlyapp.model.Genre;
import com.laa66.statlyapp.model.Image;
import com.laa66.statlyapp.model.Track;
import com.laa66.statlyapp.model.response.ResponseTracksAnalysis;
import com.laa66.statlyapp.service.LibraryAnalysisService;
import com.laa66.statlyapp.service.SpotifyAPIService;
import com.laa66.statlyapp.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LibraryAnalysisServiceImpl implements LibraryAnalysisService {

    private final StatsService statsService;
    private final SpotifyAPIService spotifyAPIService;

    @Override
    public LibraryAnalysisDTO getLibraryAnalysis(TracksDTO tracksDTO, Long userId) {
        return Optional.ofNullable(tracksDTO).map(
                tracks -> {
                    ResponseTracksAnalysis tracksAnalysis = spotifyAPIService.getTracksAnalysis(tracks);
                    Map<String, Double> mapAnalysis = getMapAnalysis(tracksAnalysis);
                    addToMap(mapAnalysis, "boringness", getBoringness(mapAnalysis));
                    addToMap(mapAnalysis, "mainstream", getMainstreamScore(tracks));
                    List<Image> images = tracks.getTracks().stream()
                            .map(track -> Optional
                                    .ofNullable(track.getAlbum().getImages().get(0))
                                    .orElseThrow(() -> new RuntimeException("Image cannot be null")))
                            .limit(22)
                            .toList();
                    if (userId != null) statsService.saveUserStats(userId, mapAnalysis);
                    return new LibraryAnalysisDTO(mapAnalysis, images);
                }
        ).orElseThrow(() -> new RuntimeException("Tracks cannot be null"));
    }

    @Override
    @Cacheable(cacheNames = "api", keyGenerator = "customKeyGenerator")
    public GenresDTO getTopGenres(long userId, String range, ArtistsDTO artistsDTO) {
        return Optional.ofNullable(artistsDTO)
                .map(ArtistsDTO::getArtists)
                .map(topArtists -> {
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
                            .toList();
                    return statsService.compareGenres(userId, new GenresDTO(transformedGenres, range, null));
                })
                .orElseThrow(() -> new RuntimeException("Artists cannot be null"));
    }

    //helpers
    private double getMainstreamScore(TracksDTO tracksDTO) {
        return new BigDecimal(Double.toString(
                tracksDTO.getTracks()
                        .stream()
                        .mapToInt(Track::getPopularity)
                        .average()
                        .orElse(0)))
                .setScale(0, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double getBoringness(Map<String, Double> mapAnalysis) {
        return !mapAnalysis.isEmpty() ?
                new BigDecimal(Double.toString(
                mapAnalysis.get("tempo")
                + (mapAnalysis.get("valence"))
                + (mapAnalysis.get("energy"))
                + (mapAnalysis.get("danceability"))))
                .setScale(0, RoundingMode.HALF_UP)
                .doubleValue() : 0.0;
    }

    private Map<String, Double> getMapAnalysis(ResponseTracksAnalysis tracksAnalysis) {
        Map<String, Double> analyzedTracks = new HashMap<>();
        int trackCount = tracksAnalysis.getTracksAnalysis().size();

        tracksAnalysis.getTracksAnalysis().forEach(
                track -> {
                    addToMap(analyzedTracks, "acousticness", track.getAcousticness() * 100);
                    addToMap(analyzedTracks, "danceability", track.getDanceability() * 100);
                    addToMap(analyzedTracks, "energy", track.getEnergy() * 100);
                    addToMap(analyzedTracks, "instrumentalness", track.getInstrumentalness() * 100);
                    addToMap(analyzedTracks, "liveness", track.getLiveness() * 100);
                    addToMap(analyzedTracks, "loudness", track.getLoudness());
                    addToMap(analyzedTracks, "speechiness", track.getSpeechiness() * 100);
                    addToMap(analyzedTracks, "tempo", track.getTempo());
                    addToMap(analyzedTracks, "valence", track.getValence() * 100);
                }
        );

        return analyzedTracks.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        value -> new BigDecimal(Double
                                .toString((value.getValue() / trackCount)))
                                .setScale(0, RoundingMode.HALF_UP)
                                .doubleValue(),
                        (first, conflict) -> first,
                        LinkedHashMap::new));
    }

    private void addToMap(Map<String, Double> map, String key, double value) {
        map.merge(key, value, Double::sum);
    }
}
